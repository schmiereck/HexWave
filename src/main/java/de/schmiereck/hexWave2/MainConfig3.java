package de.schmiereck.hexWave2;

public class MainConfig3 {

    public enum ConfigEnum {
        LifeEnvironment,
        StaticBall,
        BlockedBall,
        JumpingBall,
        BouncingBall,
        MachineBalls,
        CrashBalls,
        NoMoves,
        SlideTop
    }

    public static int HexGridXSize = 12;//10;
    public static int HexGridYSize = 3;

    public static ConfigEnum config;
    public static boolean useGravitation = true;
    public static boolean useMoveLifePart = true;
    public static boolean useBall = false;
    public static boolean useBallPush = false;

    public static final int InitialBallPartMass = 8;
    public static final double InitialBallPartEnergy = 1.0D / 2.0D;
    public static final int InitialBallPartProbability = 6 * 6 * 6 * 6 * 6;

    public static final int InitialWallPartMass = 0;
    public static final double InitialWallPartEnergy = 0.0D;
    public static final int InitialWallPartProbability = 100;

    public static int GravitationalAccelerationBP = 10;
    public static int GravitationalAccelerationCN = 10;


    public static boolean UseWalls = true;
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
            case StaticBall -> {
                useBall = true;
                BallStartXPos = new int[] { 36 };
                BallStartYPos = new int[] { 20 };
                BallStartVelocityA = new int[] { 0 };
                UseWalls = false;
                UseExtraWalls = false;
                useGravitation = false;
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
                useGravitation = false;
                UseExtraWalls = false;
            }
            case NoMoves -> {
                useMoveLifePart = false;
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