package de.schmiereck.hexWave;

public class MainConfig {
    public enum ConfigEnum {
        LifeEnvironment,
        JumpingBall,
        BouncingBall,
        ShowFields
    };

    public static ConfigEnum config;
    public static boolean useWallPushField = false;
    public static boolean useEnergy = true;
    public static boolean useGravitation = true;
    public static boolean useSunshine = true;
    public static boolean useLifeParts = true;
    //public static int lifePartsCount = 80;
    public static int lifePartsCount = 160;
    public static boolean useBall = false;
    public static boolean useShowFields = false;

    public static final double InitialLifePartEnergy = 2.0D;
    public static final double InitialSunPartEnergy = 1.0D;
    public static final double InitialWallPartEnergy = 0.0D;
    public static double PoolChildMutationRate = 0.25D;
    public static double BirthChildMutationRate = 0.1D;

    public static int LifePartOutputFieldStartAreaDistance = 0;

    private MainConfig() {
    }

    public static void initConfig(final ConfigEnum configEnum) {
        config = configEnum;
        switch (configEnum) {
            case LifeEnvironment -> {

            }
            case JumpingBall -> {
                useBall = true;
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
            }
            case BouncingBall -> {
                useBall = true;
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
            }
            case ShowFields -> {
                useShowFields = true;
                useSunshine = false;
                useLifeParts = false;
                useEnergy = false;
                useGravitation = false;
            }
        }
    }

}
