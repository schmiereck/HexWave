package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.brain.BrainService;
import de.schmiereck.hexWave.service.genom.Genom;
import de.schmiereck.hexWave.service.genom.GenomOutput;
import de.schmiereck.hexWave.service.genom.GenomSensor;
import de.schmiereck.hexWave.service.genom.GenomService;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.FieldType;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.GridNodeArea;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.service.hexGrid.PartField;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private BirthLifeService birthLifeService;

    @Autowired
    private FieldTypeService fieldTypeService;

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

    public void initializeShowFields() {
        this.lifePartList.add(this.creatShowField(10, 30, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldA));
        this.lifePartList.add(this.creatShowField(20, 30, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldB));
        this.lifePartList.add(this.creatShowField(30, 30, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldC));
        this.lifePartList.add(this.creatShowField(40, 30, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldA));
        this.lifePartList.add(this.creatShowField(50, 30, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldB));
        this.lifePartList.add(this.creatShowField(60, 30, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldC));

        final Genom genom = this.genomService.createInitialGenom();

        addGenomInputOutput(0, genom, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldA);
        addGenomInputOutput(1, genom, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldB);
        addGenomInputOutput(2, genom, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldC);
        addGenomInputOutput(3, genom, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldA);
        addGenomInputOutput(4, genom, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldB);
        addGenomInputOutput(5, genom, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldC);

        this.lifePartList.add(this.creatShowField(30, 15, genom));
    }

    private static void addGenomInputOutput(int sensorId, Genom genom, final GenomSensor.InputName inputName, final GenomOutput.OutputName outputName) {
        genom.genomSensorList.add(new GenomSensor(sensorId, inputName));
        //genom.genomOutputList.add(new GenomOutput(0, GenomOutput.OutputName.MoveA));
        //genom.genomOutputList.add(new GenomOutput(1, GenomOutput.OutputName.MoveB));
        genom.genomOutputList.add(new GenomOutput(6 + sensorId, sensorId, outputName));
    }

    public LifePart creatShowField(final int xPos, final int yPos, final GenomSensor.InputName inputName, final GenomOutput.OutputName outputName) {
        final Genom genom = this.genomService.createInitialGenom();

        int sensorId = 0;
        genom.genomSensorList.add(new GenomSensor(sensorId, inputName));
        //genom.genomOutputList.add(new GenomOutput(0, GenomOutput.OutputName.MoveA));
        //genom.genomOutputList.add(new GenomOutput(1, GenomOutput.OutputName.MoveB));
        genom.genomOutputList.add(new GenomOutput(1 + sensorId, sensorId, outputName));

        return creatShowField(xPos, yPos, genom);
    }

    @NotNull
    private LifePart creatShowField(int xPos, int yPos, Genom genom) {
        final Brain brain = this.brainService.createBrain(genom);

        final GridNode gridNode = this.hexGridService.getGridNode(xPos, yPos);

        final Part part = new Part(Part.PartType.Life, MainConfig.InitialLifePartEnergy, 8);

        gridNode.addPart(this.hexGridService.getActCellArrPos(), part);

        final PartIdentity partIdentity = this.birthLifeService.createPartIdentity();
        final LifePart lifePart = new LifePart(partIdentity, brain, gridNode, part);
        return lifePart;
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

        if (MainConfig.useWallPushField) {// && finalPosX == 32)
            this.wallPartList.stream().forEach(lifePart -> {
                this.runWallPushField(lifePart);
            });
        }

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
            if (MainConfig.useGravitation)
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

    private void runWallPushField(LifePart lifePart) {
        final Part part = lifePart.getPart();
        final GridNode gridNode = lifePart.getGridNode();
        if (part.getPartType() == Part.PartType.Wall) {
            final PartField pushPartField = new PartField(part, this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.PartPush), 1.0D);
            {
                final GridNodeArea gridNodeArea = gridNode.getGridNodeArea(Cell.Dir.BN, 0);
                gridNodeArea.addPartField(pushPartField);
            }
            {
                final GridNodeArea gridNodeArea = gridNode.getGridNodeArea(Cell.Dir.CP, 0);
                gridNodeArea.addPartField(pushPartField);
            }
        }
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
