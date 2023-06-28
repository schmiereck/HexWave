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
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.GridNodeArea;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.service.hexGrid.PartField;

import java.util.ArrayList;
import java.util.List;
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
    private PartIdentity wallPartIdentity;

    public LifeService() {
    }

    public void initialize(final int lifePartCount) {
        this.lifePartCount = lifePartCount;

        this.sunPartCount = this.hexGridService.getNodeCountX() / 28;
        this.sunGenom = this.genomService.createSunGenom();
        this.sunPartIdentity = new PartIdentity(0.5D, 0.5D, 1.0D);
        this.wallPartIdentity = new PartIdentity(0.5D, 1.0D, 1.0D);

        this.generationLifeService.initializeLifePartList(this.lifePartList, this.lifePartCount);
    }

    public void initializeByGenomList(List<Genom> genomList) {
        this.generationLifeService.initializeByGenomList(this.lifePartList, genomList);
    }

    public void initializeWalls() {
        // Left-/ Right-Walls.
        this.createWallY(0, 1, this.hexGridService.getNodeCountY() - 1);
        this.createWallY(this.hexGridService.getNodeCountX() - 1, 1, this.hexGridService.getNodeCountY() - 1);

        // Bottom-Wall.
        this.createWallX(0, this.hexGridService.getNodeCountX() - 1, this.hexGridService.getNodeCountY() - 1);
    }

    public void initializeExtraWalls() {
        final int middleXPos = this.hexGridService.getNodeCountX() / 2;
        final int leftXPos = middleXPos - 20;
        final int rightXPos = middleXPos + 20;
        final int bottomYPos = this.hexGridService.getNodeCountY() - 15;
        final int topYPos = bottomYPos - 25;

        // Middle-Wall.
        this.createWallY(middleXPos, bottomYPos, topYPos);
        this.createWallX(middleXPos - 15, middleXPos - 1, bottomYPos - 10);
        this.createWallX(middleXPos + 15, middleXPos + 1, bottomYPos - 10);
        // Bottom-Wall.
        this.createWallX(leftXPos, rightXPos, bottomYPos);
    }

    private void createWallY(final int middleXPos, final int aYPos, final int bYPos) {
        final int bottomYPos = Math.max(aYPos, bYPos);
        final int topYPos = Math.min(aYPos, bYPos);

        for (int posY = topYPos; posY <= bottomYPos; posY++) {
            this.createWall(middleXPos, posY);
        }
    }

    private void createWallX(final int aXPos, final int bXPos, final int bottomYPos) {
        final int leftXPos = Math.min(aXPos, bXPos);
        final int rightXPos = Math.max(aXPos, bXPos);

        for (int posX = leftXPos; posX <= rightXPos; posX++) {
            this.createWall(posX, bottomYPos);
        }
    }

    private void createWall(final int xPos, final int posY) {
        final GridNode leftGridNode = this.hexGridService.getGridNode(xPos, posY);
        final Part leftPart = new Part(Part.PartType.Wall, MainConfig.InitialWallPartEnergy, 0);
        final LifePart leftLifePart = new LifePart(this.wallPartIdentity, null, leftGridNode, leftPart);
        this.hexGridService.addPart(leftGridNode, new Part(Part.PartType.Wall, MainConfig.InitialWallPartEnergy, 0));
        this.wallPartList.add(leftLifePart);
    }

    public void initializeBall(final int ballXPos, final int ballYPos, final int ballStartVelocityA) {
        final Genom lifePartGenom = this.genomService.createInitialGenom();

        final Brain brain = this.brainService.createBrain(lifePartGenom);

        final GridNode gridNode = this.hexGridService.getGridNode(ballXPos, ballYPos);

        final Part part = new Part(Part.PartType.Life, MainConfig.InitialLifePartEnergy, 8);

        this.hexGridService.addPart(gridNode, part);

        final PartIdentity partIdentity = this.birthLifeService.createPartIdentity();
        final LifePart lifePart = new LifePart(partIdentity, brain, gridNode, part);

        part.getHexParticle().getVelocityHexVector().a = ballStartVelocityA;

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

        addGenomInputOutput(this.genomService.calcNextId(genom), genom, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldA);
        addGenomInputOutput(this.genomService.calcNextId(genom), genom, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldB);
        addGenomInputOutput(this.genomService.calcNextId(genom), genom, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldC);
        addGenomInputOutput(this.genomService.calcNextId(genom), genom, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldA);
        addGenomInputOutput(this.genomService.calcNextId(genom), genom, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldB);
        addGenomInputOutput(this.genomService.calcNextId(genom), genom, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldC);

        this.lifePartList.add(this.creatShowField(30, 15, genom));
    }

    private void addGenomInputOutput(int inputSensorId, Genom genom, final GenomSensor.InputName inputName, final GenomOutput.OutputName outputName) {
        genom.genomSensorList.add(new GenomSensor(inputSensorId, inputName));
        //genom.genomOutputList.add(new GenomOutput(0, GenomOutput.OutputName.MoveA));
        //genom.genomOutputList.add(new GenomOutput(1, GenomOutput.OutputName.MoveB));
        genom.genomOutputList.add(new GenomOutput(this.genomService.calcNextId(genom), inputSensorId, outputName));
    }

    public LifePart creatShowField(final int xPos, final int yPos, final GenomSensor.InputName inputName, final GenomOutput.OutputName outputName) {
        final Genom genom = this.genomService.createInitialGenom();

        int sensorId = this.genomService.calcNextId(genom);
        genom.genomSensorList.add(new GenomSensor(sensorId, inputName));
        //genom.genomOutputList.add(new GenomOutput(0, GenomOutput.OutputName.MoveA));
        //genom.genomOutputList.add(new GenomOutput(1, GenomOutput.OutputName.MoveB));
        genom.genomOutputList.add(new GenomOutput(this.genomService.calcNextId(genom), sensorId, outputName));

        return creatShowField(xPos, yPos, genom);
    }

    @NotNull
    private LifePart creatShowField(int xPos, int yPos, Genom genom) {
        final Brain brain = this.brainService.createBrain(genom);

        final GridNode gridNode = this.hexGridService.getGridNode(xPos, yPos);

        final Part part = new Part(Part.PartType.Life, MainConfig.InitialLifePartEnergy, 8);

        this.hexGridService.addPart(gridNode, part);

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
        if (MainConfig.useEat)
        this.lifePartList.stream().forEach(lifePart -> {
            this.outputLifeService.runEatOrGiveNeighbourEnergy(lifePart);
        });

        final List<LifePart> newChildLifePartList = new ArrayList<>();
        this.lifePartList.stream().forEach(lifePart -> {
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

    /**
     * alle collisions
     *      Acceleration: out +=> in, clear out
     *
     * alle
     *      Acceleration: in +=> out, clear in
     * alle move-collisions
     *      Acceleration: out +=> in, clear out
     * alle
     *      Acceleration: in +=> out, clear in
     * alle
     *      Out-Acceleration -> Velocity, clear out
     */
    public void runMoveOrCollisions() {

        if (MainConfig.useMoveLifePart) {
            this.lifePartList.stream().forEach(lifePart -> {
                if (MainConfig.useBall) printDebug(lifePart);
                //TODO this.runCollisionWithSingleDir(lifePart);
                this.moveLiveService.runMoveOrCollisionWithDirList(lifePart);
            });
        }
        if (MainConfig.useMoveSunPart)
        this.sunPartList.stream().forEach(lifePart -> {
            this.moveLiveService.runMoveOrCollisionWithDirList(lifePart);
        });
/*
        this.lifePartList.stream().forEach(lifePart -> {
            this.moveLiveService.runAccelerationAddInToOut(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.moveLiveService.runAccelerationAddInToOut(lifePart);
        });

        this.lifePartList.stream().forEach(lifePart -> {
            this.moveLiveService.runOutAccelerationToVelocity(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.moveLiveService.runOutAccelerationToVelocity(lifePart);
        });

 */
    }

    public void calcAcceleration() {
        this.lifePartList.stream().forEach(lifePart -> {
            if (MainConfig.useGravitation)
                this.accelerationLifeService.calcGravitationalAcceleration(lifePart);
            this.accelerationLifeService.calcFieldAcceleration(lifePart);
        });
    }

    public void calcNext() {
        //this.lifePartList.removeIf(lifePart -> this.generationLifeService.runDeath(lifePart));
        this.lifePartList = this.lifePartList.stream().filter(lifePart -> !this.generationLifeService.runDeath(lifePart)).collect(Collectors.toList());
        //this.sunPartList.removeIf(lifePart -> this.generationLifeService.runDeath(lifePart));
        this.sunPartList = this.sunPartList.stream().filter(lifePart -> !this.generationLifeService.runDeath(lifePart)).collect(Collectors.toList());

        if (MainConfig.useBirth) this.generationLifeService.runBirth(this.lifePartList, this.lifePartCount, MainConfig.MinLifePartCount, true);

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
        //System.out.printf("ia(a:%d b:%d c:%d) ", hexParticle.getInAccelerationHexVector().a, hexParticle.getInAccelerationHexVector().b, hexParticle.getInAccelerationHexVector().c);
        //System.out.printf("oa(a:%d b:%d c:%d) ", hexParticle.getOutAccelerationHexVector().a, hexParticle.getOutAccelerationHexVector().b, hexParticle.getOutAccelerationHexVector().c);
        System.out.printf("m(a:%d b:%d c:%d) ", hexParticle.getMoveHexVector().a, hexParticle.getMoveHexVector().b, hexParticle.getMoveHexVector().c);
        System.out.println();
    }

    private LifePart createSunPartByBrain(final Brain brain, final double energy) {
        final GridNode gridNode = this.hexGridService.searchRandomEmptyGridNode(true);

        final Part part = new Part(Part.PartType.Sun, energy, 1);

        part.getHexParticle().getVelocityHexVector().b = MainConfig.InitialSunVellocityB;
        part.getHexParticle().getVelocityHexVector().c = MainConfig.InitialSunVellocityC;

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

    public long retrievePartCount() {
        return this.lifePartList.size();
    }

}
