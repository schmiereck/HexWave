package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.brain.BrainService;
import de.schmiereck.hexWave.service.genom.Genom;
import de.schmiereck.hexWave.service.genom.GenomService;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifeService {

    @Autowired
    private HexGridService hexGridService;

    @Autowired
    private GenomService genomService;

    @Autowired
    private BrainService brainService;

    @Autowired
    private OutputLifeService outputLifeService;

    @Autowired
    private InputLifeService inputLiveService;

    @Autowired
    private MoveLifeService moveLiveService;

    @Autowired
    private GenerationLifeService generationLifeService;

    @Autowired
    private AccelerationLifeService accelerationLifeService;

    @Autowired
    private BirthLifeService birthLifeService;

    private int lifePartCount;
    private List<LifePart> lifePartList = new ArrayList<>();
    private List<LifePart> sunPartList = new ArrayList<>();
    private List<LifePart> wallPartList = new ArrayList<>();
    private int sunPartCount;
    private Genom sunGenom;
    private PartIdentity sunPartIdentity;

    public LifeService() {
    }

    public void initialize(final int lifePartCount) {
        this.lifePartCount = lifePartCount;

        this.sunPartCount = this.hexGridService.getNodeCountX() / 28;
        this.sunGenom = this.genomService.createSunGenom();
        this.sunPartIdentity = this.birthLifeService.createPartIdentity();

        this.generationLifeService.initializeLifePartList(this.lifePartList, this.lifePartCount);
    }

    public void initializeBall(final boolean useBouncingBall) {
        final Genom lifePartGenom = this.genomService.createInitialGenom();

        final Brain brain = this.brainService.createBrain(lifePartGenom);

        final int xPos, yPos;

        if (useBouncingBall) {
            xPos = (this.hexGridService.getNodeCountX() / 3) * 2;
            yPos = 30;
        } else {
            xPos = this.hexGridService.getNodeCountX() / 2;
            yPos = 36;
        }
        final GridNode gridNode = this.hexGridService.getGridNode(xPos, yPos);

        final Part part = new Part(Part.PartType.Life, MainConfig.InitialLifePartEnergy, 8);

        gridNode.addPart(this.hexGridService.getActCellArrPos(), part);

        final PartIdentity partIdentity = this.birthLifeService.createPartIdentity();

        final LifePart lifePart = new LifePart(partIdentity, brain, gridNode, part);

        if (useBouncingBall) {
            part.getHexParticle().getVelocityHexVector().a = 32;
        }

        this.lifePartList.add(lifePart);
    }

    public void addSunshine() {
        for (int sunPartPos = 0; sunPartPos < this.sunPartCount; sunPartPos++) {
            final Brain sunBrain = this.brainService.createBrain(this.sunGenom);
            this.sunPartList.add(this.createSunPartByBrain(sunBrain, MainConfig.InitialSunPartEnergy));
        }
    }

    public void runSensorInputs() {
        this.lifePartList.stream().forEach(lifePart -> {
            this.inputLiveService.calcSensorInputs(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.inputLiveService.calcSensorInputs(lifePart);
        });
    }

    public void runBrain() {
        this.lifePartList.stream().forEach(lifePart -> {
            this.runBrain(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.runBrain(lifePart);
        });
    }

    public void runOutputActionResults() {
        this.lifePartList.stream().forEach(lifePart -> {
            this.outputLifeService.runEatNeighbour(lifePart);
        });

        final List<LifePart> newChildLifePartList = new ArrayList<>();
        this.lifePartList.stream().forEach(lifePart -> {
            this.outputLifeService.runOutputMoveAcceleration(lifePart);
            this.outputLifeService.runOutputFields(newChildLifePartList, lifePart);
        });
        this.lifePartList.addAll(newChildLifePartList);

        this.sunPartList.stream().forEach(lifePart -> {
            this.outputLifeService.runOutputMoveAcceleration(lifePart);
        });
    }

    public void runMoveOrCollisions() {
        this.lifePartList.stream().forEach(lifePart -> {
            if (MainConfig.useBall) printDebug(lifePart);
            //TODO this.runCollisionWithSingleDir(lifePart);
            this.moveLiveService.runMoveOrCollisionWithDirList(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.moveLiveService.runMoveOrCollisionWithDirList(lifePart);
        });
    }

    public void calcAcceleration() {
        this.lifePartList.stream().forEach(lifePart -> {
            this.accelerationLifeService.calcGravitationalAcceleration(lifePart);
            this.accelerationLifeService.calcFieldAcceleration(lifePart);
        });
    }

    public void calcNext() {
        this.lifePartList.removeIf(lifePart -> this.generationLifeService.runDeath(lifePart));
        this.sunPartList.removeIf(lifePart -> this.generationLifeService.runDeath(lifePart));

        this.generationLifeService.runBirth(this.lifePartList, this.lifePartCount, true);

        this.hexGridService.calcNext();
    }

    private void runBrain(final LifePart lifePart) {
        final Brain brain = lifePart.getBrain();

        this.brainService.calcBrain(brain);

        if (MainConfig.useEnergy) lifePart.getPart().addEnergy(-0.01);
    }

    private static void printDebug(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        System.out.printf("p(x:%d y:%d) ", lifePart.getGridNode().getPosX(), lifePart.getGridNode().getPosY());
        System.out.printf("v(a:%d b:%d c:%d) ", hexParticle.getVelocityHexVector().a, hexParticle.getVelocityHexVector().b, hexParticle.getVelocityHexVector().c);
        System.out.printf("m(a:%d b:%d c:%d) ", hexParticle.getMoveHexVector().a, hexParticle.getMoveHexVector().b, hexParticle.getMoveHexVector().c);
        System.out.println();
    }

    private LifePart createSunPartByBrain(final Brain brain, final double energy) {
        final GridNode gridNode = this.hexGridService.searchRandomEmptyGridNode(true);

        final Part part = new Part(Part.PartType.Sun, energy, 1);

        this.hexGridService.addPart(gridNode, part);

        return new LifePart(this.sunPartIdentity, brain, gridNode, part);
    }

    public void calcGenPoolWinners() {
        this.lifePartList = this.generationLifeService.calcGenPoolWinners(this.lifePartList, this.lifePartCount);
    }

    public void initializeWalls() {

        // Left-/ Right-Walls.
        for (int posY = 0; posY < this.hexGridService.getNodeCountY(); posY++) {
            final GridNode leftGridNode = this.hexGridService.getGridNode(0, posY);
            final Part leftPart = new Part(Part.PartType.Wall, MainConfig.InitialWallPartEnergy, 0);
            final LifePart leftLifePart = new LifePart(null, null, leftGridNode, leftPart);
            leftGridNode.addPart(0, new Part(Part.PartType.Wall, MainConfig.InitialWallPartEnergy, 0));
            this.wallPartList.add(leftLifePart);

            final GridNode rightGridNode = this.hexGridService.getGridNode(this.hexGridService.getNodeCountX() - 1, posY);
            final Part rightPart = new Part(Part.PartType.Wall, MainConfig.InitialWallPartEnergy, 0);
            final LifePart rightLifePart = new LifePart(null, null, rightGridNode, rightPart);
            rightGridNode.addPart(0, new Part(Part.PartType.Wall, MainConfig.InitialWallPartEnergy, 0));
            this.wallPartList.add(rightLifePart);
        }
        // Bottom-Wall.
        for (int posX = 0; posX < this.hexGridService.getNodeCountX(); posX++) {
            final GridNode leftGridNode = this.hexGridService.getGridNode(posX, this.hexGridService.getNodeCountY() - 1);
            final Part part = new Part(Part.PartType.Wall, MainConfig.InitialWallPartEnergy, 0);
            final LifePart bottomLifePart = new LifePart(null, null, leftGridNode, part);
            leftGridNode.addPart(0, part);
            this.wallPartList.add(bottomLifePart);
        }
    }

    public List<LifePart> getWallPartList() {
        return this.wallPartList;
    }

    public List<LifePart> getSunPartList() {
        return this.sunPartList;
    }

    public List<LifePart> getLifePartList() {
        return this.lifePartList;
    }
}
