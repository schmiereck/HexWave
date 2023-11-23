package de.schmiereck.hexWave2;

import de.schmiereck.hexWave2.service.hexGrid.Particle;

public class MainConfig3 {

    public enum ConfigEnum {
        StaticBall,
        StaticBallWithField,
        StaticBallWithPotential,
        StaticBallWithPotentialAndField,
        MovingBall,
        BouncingBall,
        InteractingBallsNP,
        InteractingBallsNN,
        CrashingBalls,
        LifeEnvironment,
        BlockedBall,
        JumpingBall,
        MachineBalls,
        CrashBalls,
        SlideTop
    }

    public static boolean useGridNodeAreaRef = false;

    public static int HexGridXSize = 12;//10;
    public static int HexGridYSize = 3;


    public static int MaxPercent = 100;
    public static int MaxImpulseProb = 6 * 6 * 6;
    //public static int MaxProb = Integer.MAX_VALUE / 6 / 6;
    public static int FieldCutoffValue = 6;

    public static ConfigEnum config;
    public static boolean useBall = false;
    public static boolean useBallPush = false;

    public static final int InitialBallPartMass = 8;
    public static final double InitialBallPartEnergy = 1.0D / 2.0D;
    public static final int InitialBallPartPotentialProbability = 6 * 6 * 6;

    public static final int InitialWallPartMass = 0;
    public static final double InitialWallPartEnergy = 0.0D;
    public static final int InitialWallPartProbability = 100;

    public static int GravitationalAccelerationBP = 10;
    public static int GravitationalAccelerationCN = 10;


    public static boolean useRotation = false;

    public static boolean UseWalls = true;
    public static boolean UseExtraWalls = true;


    public static int[] BallStartXPos;
    public static int[] BallStartYPos;
    public static int[] BallStartVelocityA;
    public static Particle.PartSubType[] BallPartSubTypeArr;
    public static Particle.PartSubType[] BallFieldSubTypeArr;

    private MainConfig3() {
    }

    public static void initConfig(final ConfigEnum configEnum) {
        config = configEnum;
        switch (configEnum) {
            case LifeEnvironment -> {
                // Defaults.
            }
            case StaticBall -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocityA = new int[] { 0 };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.Nothing };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case StaticBallWithField -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocityA = new int[] { 0 };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case StaticBallWithPotential -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocityA = new int[] { 1 };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.Nothing };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case StaticBallWithPotentialAndField -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocityA = new int[] { 1 };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case MovingBall -> {
                useBall = true;
                BallStartXPos = new int[] { 36 };
                BallStartYPos = new int[] { 20 };
                BallStartVelocityA = new int[] { 6 };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case InteractingBallsNP -> {
                useBall = true;
                BallStartXPos = new int[] { 42 - 2, 42 + 2 };
                BallStartYPos = new int[] { 22, 22 };
                BallStartVelocityA = new int[] { 0, 0 };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE, Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN, Particle.PartSubType.FieldP };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case InteractingBallsNN -> {
                useBall = true;
                BallStartXPos = new int[] { 42 - 2, 42 + 2 };
                BallStartYPos = new int[] { 22, 22 };
                BallStartVelocityA = new int[] { 0, 0 };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE, Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN, Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case CrashingBalls -> {
                useBall = true;
                BallStartXPos = new int[] { 36, 46 };
                BallStartYPos = new int[] { 20, 20 };
                BallStartVelocityA = new int[] { 0, -20 };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case BlockedBall -> {
                useBall = true;
                //useShowFields = true;
                BallStartXPos = new int[] { 36 };
                BallStartYPos = new int[] { 40 };
                BallStartVelocityA = new int[] { 0 };
                UseExtraWalls = false;
            }
            case JumpingBall -> {
                useBall = true;
                BallStartXPos = new int[] { 30 };
                BallStartYPos = new int[] { 36 };
                BallStartVelocityA = new int[] { 0 };
                UseExtraWalls = false;
            }
            case BouncingBall -> {
                useBall = true;
                BallStartXPos = new int[] { 40 };
                BallStartYPos = new int[] { 30 };
                BallStartVelocityA = new int[] { 32 };
                UseExtraWalls = false;
            }
            case MachineBalls -> {
                useBall = true;
                BallStartXPos = new int[] { 30, 36, 37, 38 };
                BallStartYPos = new int[] { 30, 30, 30, 30 };
                BallStartVelocityA = new int[] { 128, 0, 0, 0 };
                UseExtraWalls = false;
            }
            case CrashBalls -> {
                useBall = true;
                BallStartXPos = new int[] { 30, 38,
                                            30, 38,};
                BallStartYPos = new int[] { 20, 20,
                                            25, 25 };
                BallStartVelocityA = new int[] { 128, -128,
                                                 128, -64 };
                UseExtraWalls = false;
            }
            case SlideTop -> {
                useBall = true;
                useBallPush = true;
                BallStartXPos = new int[] { 30 };
                BallStartYPos = new int[] { 36 };
                BallStartVelocityA = new int[] { 0 };
            }
            /*
            Neue Scenarien für Beschleunigung
                    AccelerationGravitationFreeFall
                    AccelerationGravitationCollisionOnePart
                    AccelerationGravitationCollisionOnePartAndWall
                    AccelerationGravitationCollisionRowOfParts
                    AccelerationGravitationCollisionRowOfPartsAnWall
                    NurAusgangsSpeed Ohne Grav...
             */
        }
    }

}
