package de.schmiereck.hexWave;

public class MainConfig3 {

    public enum ConfigEnum {
        LifeEnvironment,
        BlockedBall,
        JumpingBall,
        BouncingBall,
        MachineBalls,
        CrashBalls,
        ShowFields,
        OnlySun,
        NoMoves,
        SlideTop
    }

    public static int HexGridXSize = 12;//10;
    public static int HexGridYSize = 3;

    public static ConfigEnum config;
    public static boolean useWallPushField = false;
    public static boolean useEnergy = true;
    public static boolean useGravitation = true;
    public static boolean useSunshine = true;
    public static boolean useLifeParts = true;
    public static boolean useBirth = true;
    public static boolean useBirthOutput = true;
    public static boolean useMoveLifePart = true;
    public static boolean useMoveSunPart = true;
    public static boolean useEat = true;
    //public static int lifePartsCount = 80;
    public static int LifePartsCount = 30+18;
    public static int MinLifePartCount = 30+15;
    public static boolean useBall = false;
    public static boolean useBallPush = false;
    public static boolean useShowFields = false;

    public static final double InitialLifePartEnergy = 1.0D / 2.0D;
    public static final double InitialSunPartEnergy = 1.0D / 2.3D;
    public static final double InitialWallPartEnergy = 0.0D;
    public static double EnergyCostRunBrain = 1.0D / 200.0D;

    public static int MaxLifePartStepCounter = 1200;

    public static int InitialSunVellocityB = 2*50;
    public static int InitialSunVellocityC = -10*70;
    public static double PoolChildMutationRate = 0.25D;
    public static double BirthChildMutationRate = 0.1D;
    public static double FieldVelocityDiffFactor = 10.0D;
    public static int GravitationalAccelerationBP = 10;
    public static int GravitationalAccelerationCN = 10;


    public static int LifePartOutputFieldStartAreaDistance = 0;
    public static double OutputAccelerationFieldFactor = 4.0D;
    public static double PartMaxEnergy = 1.0D;

    public static boolean UseExtraWalls = true;


    public static int[] BallStartXPos;
    public static int[] BallStartYPos;
    public static int[] BallStartVelocityA;

    private MainConfig3() {
    }

    public static void initConfig(final ConfigEnum configEnum) {
        config = configEnum;
        switch (configEnum) {
            case LifeEnvironment -> {
                // Defaults.
            }
            case BlockedBall -> {
                useBall = true;
                BallStartXPos = new int[] { 36 };
                BallStartYPos = new int[] { 40 };
                BallStartVelocityA = new int[] { 0 };
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
                UseExtraWalls = false;
            }
            case JumpingBall -> {
                useBall = true;
                BallStartXPos = new int[] { 30 };
                BallStartYPos = new int[] { 36 };
                BallStartVelocityA = new int[] { 0 };
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
                UseExtraWalls = false;
            }
            case BouncingBall -> {
                useBall = true;
                BallStartXPos = new int[] { 40 };
                BallStartYPos = new int[] { 30 };
                BallStartVelocityA = new int[] { 32 };
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
                UseExtraWalls = false;
            }
            case MachineBalls -> {
                useBall = true;
                BallStartXPos = new int[] { 30, 36, 37, 38 };
                BallStartYPos = new int[] { 30, 30, 30, 30 };
                BallStartVelocityA = new int[] { 128, 0, 0, 0 };
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
                useGravitation = false;
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
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
                useGravitation = false;
                UseExtraWalls = false;
            }
            case ShowFields -> {
                useShowFields = true;
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
                useGravitation = false;
            }
            case OnlySun -> {
                useLifeParts = false;
            }
            case NoMoves -> {
                useBirthOutput = false;
                useSunshine = false;
                useMoveLifePart = false;
                useMoveSunPart = false;
                useEat = false;
            }
            case SlideTop -> {
                useBall = true;
                useBallPush = true;
                BallStartXPos = new int[] { 30 };
                BallStartYPos = new int[] { 36 };
                BallStartVelocityA = new int[] { 0 };
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
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
