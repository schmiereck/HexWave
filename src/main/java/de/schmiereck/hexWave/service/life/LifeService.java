package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.brain.BrainService;
import de.schmiereck.hexWave.service.genom.Genom;
import de.schmiereck.hexWave.service.genom.GenomService;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.FieldType;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.GridNodeArea;
import de.schmiereck.hexWave.service.hexGrid.GridNodeAreaRef;
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

        this.generationLifeService.initializeLifePartList(this.lifePartList, this.lifePartCount);
    }

    public void initializeBall() {
        final Genom lifePartGenom = this.genomService.createInitialGenom();

        final Brain brain = this.brainService.createBrain(lifePartGenom);

        final GridNode gridNode = this.hexGridService.getGridNode(this.hexGridService.getNodeCountX() / 2, 36);

        final Part part = new Part(Part.PartType.Life, this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.Part), MainConfig.InitialLifePartEnergy, false, 8);

        gridNode.addPart(this.hexGridService.getActCellArrPos(), part);

        this.lifePartList.add(new LifePart(brain, gridNode, part));
    }

    public void addSunshine() {
        for (int sunPartPos = 0; sunPartPos < this.hexGridService.getNodeCountX() / 28; sunPartPos++) {
            final Brain sunBrain = this.brainService.createBrain(this.sunGenom);
            this.sunPartList.add(this.createSunPartByBrain(sunBrain, MainConfig.InitialSunPartEnergy));
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
            this.inputLiveService.runNeighbourSensors(lifePart);
        });
    }

    public void runOutputActionResults() {
        this.lifePartList.stream().forEach(lifePart -> {
            this.outputLifeService.runEatNeighbour(lifePart);
        });
        this.lifePartList.stream().forEach(lifePart -> {
            this.outputLifeService.runMoveAcceleration(lifePart);
            this.outputLifeService.runOutputFields(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.outputLifeService.runMoveAcceleration(lifePart);
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
        brainService.calcBrain(brain);
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

        final Part part = new Part(Part.PartType.Sun, this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.Sun), energy, false, 1);

        gridNode.addPart(this.hexGridService.getActCellArrPos(), part);

        return new LifePart(brain, gridNode, part);
    }

    public void calcGenPoolWinners() {
        this.lifePartList = this.generationLifeService.calcGenPoolWinners(this.lifePartList, this.lifePartCount);
    }
}
