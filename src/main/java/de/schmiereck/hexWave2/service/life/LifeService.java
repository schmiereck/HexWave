package de.schmiereck.hexWave2.service.life;

import de.schmiereck.hexWave2.MainConfig3;
import de.schmiereck.hexWave2.math.HexParticle;
import de.schmiereck.hexWave2.service.hexGrid.Cell;
import de.schmiereck.hexWave2.service.hexGrid.GridNode;
import de.schmiereck.hexWave2.service.hexGrid.GridNodeArea;
import de.schmiereck.hexWave2.service.hexGrid.HexGridService;
import de.schmiereck.hexWave2.service.hexGrid.Part;
import de.schmiereck.hexWave2.service.hexGrid.PartField;

import java.util.ArrayList;
import java.util.List;
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
    private OutputLifeService outputLifeService;

    @Autowired
    private InputLifeService inputLiveService;

    @Autowired
    private MoveLifeService moveLiveService;

    @Autowired
    private AccelerationLifeService accelerationLifeService;

    @Autowired
    private FieldTypeService fieldTypeService;

    private final Random rnd = new Random();

    private int lifePartCount;
    private List<LifePart> lifePartList = new ArrayList<>();
    private List<LifePart> sunPartList = new ArrayList<>();
    private List<LifePart> wallPartList = new ArrayList<>();
    private int sunPartCount;
    private PartIdentity sunPartIdentity;

    public LifeService() {
    }

    public void initialize(final int lifePartCount) {
        this.lifePartCount = lifePartCount;

        this.sunPartCount = this.hexGridService.getNodeCountX() / 28;
        this.sunPartIdentity = new PartIdentity(0.5D, 0.5D, 1.0D);

        //this.generationLifeService.initializeLifePartList(this.lifePartList, this.lifePartCount);
    }

    public void initializeBall(final int ballXPos, final int ballYPos, final int ballStartVelocityA, final boolean useBallPush) {
        if (useBallPush) {
            //initializePushFields();
        }

        final GridNode gridNode = this.hexGridService.getGridNode(ballXPos, ballYPos);

        final Part part = new Part(Part.PartType.Life, MainConfig3.InitialLifePartEnergy, 8);

        final PartIdentity partIdentity = this.createPartIdentity();
        final LifePart lifePart = new LifePart(partIdentity, gridNode, part, 0);

        part.getHexParticle().getVelocityHexVector().a = ballStartVelocityA;

        this.addLifePart(lifePart);
    }

    public void addLifePart(final LifePart lifePart) {
        this.hexGridService.addPart(lifePart.getGridNode(), lifePart.getPart());
        this.lifePartList.add(lifePart);
    }

    public void addWallLifePart(final LifePart wallLifePart) {
        this.hexGridService.addPart(wallLifePart.getGridNode(), wallLifePart.getPart());
        this.wallPartList.add(wallLifePart);
    }

    public void initializeShowFields() {
        /*
        this.lifePartList.add(this.creatShowField(10, 30, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldA));
        this.lifePartList.add(this.creatShowField(20, 30, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldB));
        this.lifePartList.add(this.creatShowField(30, 30, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldC));
        this.lifePartList.add(this.creatShowField(40, 30, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldA));
        this.lifePartList.add(this.creatShowField(50, 30, GenomSensor.InputName.Negative, GenomOutput.OutputName.ComFieldB));
        this.lifePartList.add(this.creatShowField(60, 30, GenomSensor.InputName.Positive, GenomOutput.OutputName.ComFieldC));
        */

        this.lifePartList.add(this.creatShowField(30, 15));
    }

    @NotNull
    private LifePart creatShowField(int xPos, int yPos) {
        final GridNode gridNode = this.hexGridService.getGridNode(xPos, yPos);

        final Part part = new Part(Part.PartType.Life, MainConfig3.InitialLifePartEnergy, 8);

        this.hexGridService.addPart(gridNode, part);

        final PartIdentity partIdentity = this.createPartIdentity();
        final LifePart lifePart = new LifePart(partIdentity, gridNode, part, 0);
        return lifePart;
    }

    public void runLife() {
        // Sensor Inputs.
        this.runSensorInputs();

        // Calc Brain.
        this.runBrain();

        // Output Results.

        if (MainConfig3.useSunshine) this.addSunshine();
        this.runOutputActionResults();

        // 1. Part is accelerated (fields, gravity) += In-Acceleration
        this.calcOutAcceleration();

        // 2. Blocked: Part passes out-acceleration/mass in direction to neighbor as in-acceleration.
        //    Out acceleration is not set to 0.
        this.calcAddOutAccelerationToNeigboursIn();

        // 4. In-Acceleration -> Out-Acceleration, In-Acceleration = 0
        this.calcAddIn2OutAcceleration();

        // 3. Move: Out acceleration/mass is converted to velocity.
        //this.calcOutAccelerationToVelocity();

        this.runMoveOrCollisions();

        this.calcClearOutAcceleration();

        this.calcNext();
    }

    public void addSunshine() {
        for (int sunPartPos = 0; sunPartPos < this.sunPartCount; sunPartPos++) {
            this.sunPartList.add(this.createSunPartByBrain(MainConfig3.InitialSunPartEnergy));
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
        //if (MainConfig3.useEat)
        //this.lifePartList.stream().forEach(lifePart -> {
        //    this.outputLifeService.runEatOrGiveNeighbourEnergy(lifePart);
            //});

        final List<LifePart> newChildLifePartList = new ArrayList<>();
        this.lifePartList.stream().forEach(lifePart -> {
            this.outputLifeService.runOutputFields(newChildLifePartList, lifePart);
        });
        this.lifePartList.addAll(newChildLifePartList);

        if (MainConfig3.useWallPushField) {// && finalPosX == 32)
            this.wallPartList.stream().forEach(lifePart -> {
                this.runWallPushField(lifePart);
            });
        }

        //this.sunPartList.stream().forEach(lifePart -> {
        //    this.outputLifeService.runOutputMoveAcceleration(lifePart);
        //});
    }

    public void runMoveOrCollisions() {

        if (MainConfig3.useMoveLifePart) {
            this.lifePartList.stream().forEach(lifePart -> {
                //TODO this.runCollisionWithSingleDir(lifePart);
                this.moveLiveService.runMoveOrCollisionWithDirList(lifePart);
                if (MainConfig3.useBall) printDebugVelocity(lifePart);
                if (MainConfig3.useBall) printDebugMove(lifePart);
                if (MainConfig3.useBall) printDebugEnd();
            });
        }
        if (MainConfig3.useMoveSunPart)
        this.sunPartList.stream().forEach(lifePart -> {
            this.moveLiveService.runMoveOrCollisionWithDirList(lifePart);
        });
    }

    public void calcAddIn2OutAcceleration() {
        this.lifePartList.stream().forEach(lifePart -> {
            this.moveLiveService.runAccelerationAddInToOut(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.moveLiveService.runAccelerationAddInToOut(lifePart);
        });

/*
        this.lifePartList.stream().forEach(lifePart -> {
            this.moveLiveService.runOutAccelerationToVelocity(lifePart);
        });
        this.sunPartList.stream().forEach(lifePart -> {
            this.moveLiveService.runOutAccelerationToVelocity(lifePart);
        });
 */
    }

    public void calcOutAcceleration() {
        this.lifePartList.stream().forEach(lifePart -> {
            if (MainConfig3.useBall) printDebugStart(lifePart);
            if (MainConfig3.useGravitation)
                this.accelerationLifeService.calcGravitationalAcceleration(lifePart);
            this.accelerationLifeService.calcFieldOutAcceleration(lifePart);
        });
    }

    public void calcAddOutAccelerationToNeigboursIn() {
        this.lifePartList.stream().forEach(lifePart -> {
                this.accelerationLifeService.calcAddOutAccelerationToNeigboursIn(lifePart);
            if (MainConfig3.useBall) printDebugInAcceleration(lifePart);
            if (MainConfig3.useBall) printDebugOutAcceleration(lifePart);
        });
    }

    public void calcOutAccelerationToVelocity() {
        this.lifePartList.stream().forEach(lifePart -> {
                this.accelerationLifeService.calcOutAccelerationToVelocity(lifePart);
            if (MainConfig3.useBall) printDebugVelocity(lifePart);
        });
    }

    public void calcClearOutAcceleration() {
        this.lifePartList.stream().forEach(lifePart -> {
                this.accelerationLifeService.calcClearOutAcceleration(lifePart);
        });
    }

    public void calcNext() {
        //this.lifePartList = this.lifePartList.stream().filter(lifePart -> !this.generationLifeService.runEnergyDeath(lifePart)).collect(Collectors.toList());

        //this.sunPartList = this.sunPartList.stream().filter(lifePart -> !this.generationLifeService.runEnergyDeath(lifePart)).collect(Collectors.toList());

        //final int stepCount = this.hexGridService.retrieveStepCount();
        //final List<LifePart> deathLifePartList = this.lifePartList.stream().filter(lifePart -> this.generationLifeService.runAgeDeath(lifePart, stepCount)).collect(Collectors.toList());
        //this.lifePartList.removeAll(deathLifePartList);

        //deathLifePartList.stream().forEach(lifePart -> {
        //    this.lifePartList.add(this.birthLifeService.createChildLifePart(lifePart));
        //    this.lifePartList.add(this.birthLifeService.createChildLifePart(lifePart));
        //});

        //if (MainConfig3.useBirth) this.generationLifeService.runBirth(this.lifePartList, this.lifePartCount, MainConfig3.MinLifePartCount, true);

        this.hexGridService.calcNext();
    }

    private void runBrain(final LifePart lifePart) {
        //final Brain brain = lifePart.getBrain();

        //this.brainService.calcBrain(brain);

        if (MainConfig3.useEnergy) lifePart.getPart().subEnergy(MainConfig3.EnergyCostRunBrain);
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
        printDebugStart(lifePart);
        printDebugVelocity(lifePart);
        printDebugInAcceleration(lifePart);
        printDebugOutAcceleration(lifePart);
        printDebugMove(lifePart);
        printDebugEnd();
    }

    private static void printDebugStart(final LifePart lifePart) {
        System.out.printf("p(x:%d y:%d) ", lifePart.getGridNode().getPosX(), lifePart.getGridNode().getPosY());
    }

    private static void printDebugVelocity(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        System.out.printf("v(a:%d b:%d c:%d) ", hexParticle.getVelocityHexVector().a, hexParticle.getVelocityHexVector().b, hexParticle.getVelocityHexVector().c);
    }

    private static void printDebugInAcceleration(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        System.out.printf("ia(a:%d b:%d c:%d) ", hexParticle.getInAccelerationHexVector().a, hexParticle.getInAccelerationHexVector().b, hexParticle.getInAccelerationHexVector().c);
    }

    private static void printDebugOutAcceleration(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        System.out.printf("oa(a:%d b:%d c:%d) ", hexParticle.getOutAccelerationHexVector().a, hexParticle.getOutAccelerationHexVector().b, hexParticle.getOutAccelerationHexVector().c);
    }

    private static void printDebugMove(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        System.out.printf("vm(a:%d b:%d c:%d) ", hexParticle.getVMoveHexVector().a, hexParticle.getVMoveHexVector().b, hexParticle.getVMoveHexVector().c);
        System.out.printf("am(a:%d b:%d c:%d) ", hexParticle.getAMoveHexVector().a, hexParticle.getAMoveHexVector().b, hexParticle.getAMoveHexVector().c);
    }

    private static void printDebugEnd() {
        System.out.println();
    }

    private LifePart createSunPartByBrain(final double energy) {
        final int stepCount = this.hexGridService.retrieveStepCount();
        final GridNode gridNode = this.hexGridService.searchRandomEmptyGridNode(true);

        final Part part = new Part(Part.PartType.Sun, energy, 1);

        part.getHexParticle().getVelocityHexVector().b = MainConfig3.InitialSunVellocityB;
        part.getHexParticle().getVelocityHexVector().c = MainConfig3.InitialSunVellocityC;

        this.hexGridService.addPart(gridNode, part);

        return new LifePart(this.sunPartIdentity, gridNode, part, stepCount);
    }

    public void calcGenPoolWinners() {
        //this.lifePartList = this.generationLifeService.calcGenPoolWinners(this.lifePartList, this.lifePartCount);
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

    public PartIdentity createPartIdentity() {
        return new PartIdentity(this.calcChildPartIdentityValue(),
                this.calcChildPartIdentityValue(),
                this.calcChildPartIdentityValue());
    }

    private double calcChildPartIdentityValue() {
        return (this.rnd.nextDouble() * 2.0D) - 1.0D;
    }

}
