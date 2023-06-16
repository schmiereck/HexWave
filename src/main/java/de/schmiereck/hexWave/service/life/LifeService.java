package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.brain.BrainService;
import de.schmiereck.hexWave.service.genom.Genom;
import de.schmiereck.hexWave.service.genom.GenomOutput;
import de.schmiereck.hexWave.service.genom.GenomService;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.utils.HexMathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifeService {
    public static final double InitialLifePartEnergy = 2.0D;
    public static final double InitialSunPartEnergy = 1.0D;
    public static final double InitialWallPartEnergy = 0.0D;

    @Autowired
    private HexGridService hexGridService;

    @Autowired
    private GenomService genomService;

    @Autowired
    private BrainService brainService;
    private int lifePartCount;
    private List<LifePart> lifePartList = new ArrayList<>();
    private List<LifePart> sunPartList = new ArrayList<>();
    private final Random rnd = new Random();
    private int sunPartCount;
    private Genom sunGenom;

    public LifeService() {
    }

    public void initialize(final int lifePartCount) {
        this.lifePartCount = lifePartCount;

        this.sunPartCount = this.hexGridService.getNodeCountX() / 20;
        this.sunGenom = this.genomService.createSunGenom();

        final Genom genom = this.genomService.createInitialGenom();

        for (int lifePartPos = 0; lifePartPos < lifePartCount; lifePartPos++) {
            final Brain brain = this.brainService.createBrain(genom);

            this.lifePartList.add(this.createLifePartByBrain(brain, this.rnd.nextDouble(InitialLifePartEnergy / 2.0D, InitialLifePartEnergy)));
        }
    }

    public void addSunshine() {
        for (int sunPartPos = 0; sunPartPos < this.hexGridService.getNodeCountX() / 28; sunPartPos++) {
            final Brain sunBrain = this.brainService.createBrain(this.sunGenom);
            this.sunPartList.add(this.createSunPartByBrain(sunBrain, InitialSunPartEnergy));
        }
    }

    public void runBrain() {
        this.lifePartList.stream().forEach(lifePart -> {
            this.runBrain(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.runBrain(lifePart);
        });
    }

    public void runSensorInputs() {
        this.lifePartList.stream().forEach(lifePart -> {
            this.runNeighbourSensors(lifePart);
        });
    }

    public void runOutputActionResults() {
        this.lifePartList.stream().forEach(lifePart -> {
            this.runEatNeighbour(lifePart);
        });
        this.lifePartList.stream().forEach(lifePart -> {
            this.runMove(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.runMove(lifePart);
        });
    }

    public void calcNext() {
        this.lifePartList.removeIf(lifePart -> this.runDeath(lifePart));
        this.sunPartList.removeIf(lifePart -> this.runDeath(lifePart));

        this.runBirth();

        this.lifePartList.stream().forEach(lifePart -> {
            this.calcGravitationalAcceleration(lifePart);
        });

        this.hexGridService.calcNext();
    }

    private void calcGravitationalAcceleration(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        hexParticle.velocityHexVector.c -= 1;
        hexParticle.velocityHexVector.b += 1;
    }

    private void runBirth() {
        if (this.lifePartList.size() < (this.lifePartCount - (this.lifePartCount / 4))) {
            while (this.lifePartList.size() < this.lifePartCount) {
                final LifePart childLifePart;
                if (this.lifePartList.size() == 0) {
                    final Genom genom = this.genomService.createInitialGenom();
                    final Brain brain = this.brainService.createBrain(genom);
                    childLifePart = this.createLifePartByBrain(brain, InitialLifePartEnergy);
                } else {
                    final LifePart parentLifePart = this.lifePartList.get(this.rnd.nextInt(this.lifePartList.size()));
                    childLifePart = this.createChildLifePart(parentLifePart);
                }
                this.lifePartList.add(childLifePart);
            }
        }
    }

    private boolean runDeath(final LifePart lifePart) {
        final boolean removed;
        final Part part = lifePart.getPart();

        if (part.getEnergy() <= 0.0D) {
            final GridNode gridNode = lifePart.getGridNode();
            this.hexGridService.removePart(gridNode, part);
            removed = true;
        } else {
            removed = false;
        }
        return removed;
    }

    private void runNeighbourSensors(final LifePart lifePart) {
        // Sensor: For each direction the Neighbour-Part (Part-Type, Field(?)).
        final GridNode gridNode = lifePart.getGridNode();
        final Brain brain = lifePart.getBrain();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            final GridNode neighbourGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
            final List<Part> neighbourPartList = neighbourGridNode.getPartList(this.hexGridService.getActCellArrPos());
            if (!neighbourPartList.isEmpty()) {
                final Part neighbourPart = neighbourPartList.get(0);
                brain.setNeigbourPartTypeInput(dir, neighbourPart.getPartType());
            } else {
                brain.setNeigbourPartTypeInput(dir, null);
            }
        }
    }

    private void runEatNeighbour(final LifePart lifePart) {
        // Output: Eat the Neighbour-Part or not.
        final GridNode gridNode = lifePart.getGridNode();
        final Brain brain = lifePart.getBrain();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            final double brainOutput = brain.getEatNeighbourOutput(dir);
            if (brainOutput > 0.5D) {
                final GridNode neighbourGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
                final List<Part> neighbourPartList = neighbourGridNode.getPartList(this.hexGridService.getActCellArrPos());
                if (!neighbourPartList.isEmpty()) {
                    final Part neighbourPart = neighbourPartList.get(0);
                    final double energy = neighbourPart.getEnergy();
                    neighbourPart.setEnergy(0.0D);

                    if (energy > 0.0D) {
                        final Part part = lifePart.getPart();
                        part.addEnergy(energy);
                    }
                }
            }
        }
    }

    private void runBrain(final LifePart lifePart) {
        final Brain brain = lifePart.getBrain();
        brainService.calcBrain(brain);
        lifePart.getPart().addEnergy(-0.01);
    }

    private void runMove(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        HexMathUtils.transferVelocityToMove(hexParticle.getVelocityHexVector(), hexParticle.getMoveHexVector());
        final Cell.Dir moveDir = HexMathUtils.determineNextMove(hexParticle.getMoveHexVector());

        if (Objects.nonNull(moveDir)) {
            final Part blockingPart = this.movePart(lifePart, moveDir);
            if (Objects.isNull(blockingPart)) {
                HexMathUtils.calcNextMove(moveDir, hexParticle.getMoveHexVector());
            } else {
                if (blockingPart.getPartType() == Part.PartType.Wall) {
                    HexMathUtils.calcElasticCollisionWithSolidWall(hexParticle, moveDir);
                } else {
                    HexMathUtils.calcElasticCollision(hexParticle, moveDir, blockingPart.getHexParticle());
                }
            }
        } else {
            final Brain brain = lifePart.getBrain();
            final double moveA = brain.getOutput(GenomOutput.OutputName.MoveA);
            final double moveB = brain.getOutput(GenomOutput.OutputName.MoveB);
            final double moveC = brain.getOutput(GenomOutput.OutputName.MoveC);

            final Cell.Dir dir;

            if (Math.abs(moveA) > Math.abs(moveB)) {
                if (Math.abs(moveA) > Math.abs(moveC)) {
                    dir = this.calcMoveDir(moveA, Cell.Dir.AP, Cell.Dir.AN);
                } else {
                    dir = this.calcMoveDir(moveC, Cell.Dir.CP, Cell.Dir.CN);
                }
            } else {
                if (Math.abs(moveB) > Math.abs(moveC)) {
                    dir = this.calcMoveDir(moveB, Cell.Dir.BP, Cell.Dir.BN);
                } else {
                    dir = this.calcMoveDir(moveC, Cell.Dir.CP, Cell.Dir.CN);
                }
            }
            if (Objects.nonNull(dir)) {
                this.movePart(lifePart, dir);
                lifePart.getPart().addEnergy(-0.01);
            }
        }

    }

    private Part movePart(final LifePart lifePart, final Cell.Dir dir) {
        final Part blockingPart;
        final GridNode gridNode = lifePart.getGridNode();
        final GridNode newGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
        if (newGridNode.getPartList(this.hexGridService.getActCellArrPos()).isEmpty()) {
            final Part part = lifePart.getPart();
            this.hexGridService.removePart(gridNode, part);
            this.hexGridService.addPart(newGridNode, part);
            lifePart.setGridNode(newGridNode);
            blockingPart = null;
        } else {
            blockingPart = newGridNode.getPartList(this.hexGridService.getActCellArrPos()).get(0);
        }
        return blockingPart;
    }

    private Cell.Dir calcMoveDir(double moveA, Cell.Dir pDir, Cell.Dir nDir) {
        final Cell.Dir dir;
        if (moveA > 0.0D) {
            dir = pDir;
        } else {
            if (moveA < 0.0D) {
                dir = nDir;
            } else {
                dir = null;
            }
        }
        return dir;
    }

    public void calcGenPoolWinners() {
        this.lifePartList.stream().forEach(lifePart -> lifePart.getGridNode().removePart(this.hexGridService.getActCellArrPos(), lifePart.getPart()));

        final int maxPosX = this.hexGridService.getNodeCountX() / 2;
        final List<LifePart> winnerLifePartList = this.lifePartList.stream().filter(lifePart -> lifePart.getGridNode().getPosX() > maxPosX).collect(Collectors.toList());
        final List<LifePart> youngLifePartList = winnerLifePartList.subList(winnerLifePartList.size() / 2, winnerLifePartList.size());

        if (youngLifePartList.isEmpty()) {
            youngLifePartList.add(this.lifePartList.get(0));
        }

        youngLifePartList.stream().forEach(lifePart -> {
            final GridNode gridNode = this.searchRandomEmptyGridNode(false);
            gridNode.addPart(this.hexGridService.getActCellArrPos(), lifePart.getPart());
            lifePart.setGridNode(gridNode);
        });

        final List<LifePart> childLifePartList = new ArrayList<>();

        for (int pos = 0; pos < (this.lifePartCount - youngLifePartList.size()); pos++) {
            final LifePart youngLifePart = youngLifePartList.get(pos % youngLifePartList.size());
            final LifePart childLifePart = createChildLifePart(youngLifePart);

            childLifePartList.add(childLifePart);
        }

        youngLifePartList.addAll(childLifePartList);

        this.lifePartList = youngLifePartList;
    }

    @NotNull
    private LifePart createChildLifePart(final LifePart parentLifePart) {
        final Brain youngLifePartBrain = parentLifePart.getBrain();
        final Genom genom = youngLifePartBrain.getGenom();
        final Genom newGenom = this.genomService.createMutatedGenom(genom, 0.25D);

        final Brain childBrain = this.brainService.createBrain(newGenom);
        final LifePart childLifePart = this.createLifePartByBrain(childBrain, this.rnd.nextDouble(InitialLifePartEnergy / 2.0D, InitialLifePartEnergy));
        return childLifePart;
    }

    private LifePart createLifePartByBrain(final Brain brain, final double energy) {
        final GridNode gridNode = this.searchRandomEmptyGridNode(false);

        final Part part = new Part(Part.PartType.Life, this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.Part), energy, true);

        gridNode.addPart(this.hexGridService.getActCellArrPos(), part);

        return new LifePart(brain, gridNode, part);
    }

    private LifePart createSunPartByBrain(final Brain brain, final double energy) {
        final GridNode gridNode = this.searchRandomEmptyGridNode(true);

        final Part part = new Part(Part.PartType.Sun, this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.Sun), energy, false);

        gridNode.addPart(this.hexGridService.getActCellArrPos(), part);

        return new LifePart(brain, gridNode, part);
    }

    private GridNode searchRandomEmptyGridNode(final boolean onTop) {
        GridNode gridNode;

        do {
            final int posX = this.rnd.nextInt(this.hexGridService.getNodeCountX() - 2) + 1;
            final int posY;
            if (onTop) {
                posY = 0;
            } else {
                posY = this.rnd.nextInt(this.hexGridService.getNodeCountY() - 2) + 1;
            }
            gridNode = this.hexGridService.getGridNode(posX, posY);
        }
        while (!gridNode.getPartList(0).isEmpty());

        return gridNode;
    }
}
