package de.schmiereck.hexWave2.service.life;

import de.schmiereck.hexWave2.MainConfig3;
import de.schmiereck.hexWave2.math.HexParticle;
import de.schmiereck.hexWave2.service.hexGrid.GridNode;
import de.schmiereck.hexWave2.service.hexGrid.HexGridService;
import de.schmiereck.hexWave2.service.hexGrid.Part;
import de.schmiereck.hexWave2.service.hexGrid.Particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifeService {


    @Autowired
    private HexGridService hexGridService;

    @Autowired
    private AccelerationLifeService accelerationLifeService;

    private final Random rnd = new Random();

    private List<LifePart> lifePartList = new ArrayList<>();
    private List<LifePart> wallPartList = new ArrayList<>();

    public LifeService() {
    }

    public void initialize() {
    }

    public void initializeBall(final int ballXPos, final int ballYPos, final int ballStartVelocityA, final boolean useBallPush) {
        if (useBallPush) {
            //initializePushFields();
        }

        final GridNode gridNode = this.hexGridService.getGridNode(ballXPos, ballYPos);

        final Particle ballParticle = new Particle();
        final int probability = MainConfig3.InitialBallPartProbability;
        final Part ballPart = new Part(ballParticle, Part.PartType.Life, MainConfig3.InitialBallPartEnergy, MainConfig3.InitialBallPartMass, probability, 1);

        final PartIdentity partIdentity = this.createPartIdentity();
        final LifePart lifePart = new LifePart(partIdentity, gridNode, ballPart);

        ballPart.getHexParticle().getVelocityHexVector().a = ballStartVelocityA;

        this.addLifePart(lifePart);
    }

    public void addLifePart(final LifePart lifePart) {
        this.hexGridService.addActPart(lifePart.getGridNode(), lifePart.getPart());
        this.lifePartList.add(lifePart);
    }

    public void addWallLifePart(final LifePart wallLifePart) {
        this.hexGridService.addActPart(wallLifePart.getGridNode(), wallLifePart.getPart());
        this.wallPartList.add(wallLifePart);
    }

    public void runLife() {
        // Output Results.

        // 1. Part is accelerated (fields, gravity) += In-Acceleration
        this.calcOutAcceleration();

        // 2. Blocked: Part passes out-acceleration/mass in direction to neighbor as in-acceleration.
        //    Out acceleration is not set to 0.
        this.calcAddOutAccelerationToNeigboursIn();

        this.runMoveOrCollisions();

        this.calcClearOutAcceleration();

        this.calcNext();
    }

    public void runMoveOrCollisions() {

        if (MainConfig3.useMoveLifePart) {
            this.lifePartList.stream().forEach(lifePart -> {
                //TODO this.runCollisionWithSingleDir(lifePart);
                if (MainConfig3.useBall) printDebugVelocity(lifePart);
                if (MainConfig3.useBall) printDebugMove(lifePart);
                if (MainConfig3.useBall) printDebugEnd();
            });
        }
    }

    public void calcOutAcceleration() {
        this.lifePartList.stream().forEach(lifePart -> {
            if (MainConfig3.useBall) printDebugStart(lifePart);
            if (MainConfig3.useGravitation)
                this.accelerationLifeService.calcGravitationalAcceleration(lifePart);
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

    public void calcGenPoolWinners() {
        //this.lifePartList = this.generationLifeService.calcGenPoolWinners(this.lifePartList, this.lifePartCount);
    }

    public List<LifePart> getWallPartList() {
        return this.wallPartList;
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
