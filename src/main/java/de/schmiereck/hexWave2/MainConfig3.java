package de.schmiereck.hexWave2;

import static de.schmiereck.hexWave2.MainConfig3.StartVelocity.MovingLeftWithPotential;
import static de.schmiereck.hexWave2.MainConfig3.StartVelocity.MovingRight;
import static de.schmiereck.hexWave2.MainConfig3.StartVelocity.MovingRightWithPotential;
import static de.schmiereck.hexWave2.MainConfig3.StartVelocity.Static;
import static de.schmiereck.hexWave2.MainConfig3.StartVelocity.StaticPotential;

import de.schmiereck.hexWave2.service.hexGrid.Particle;

public class MainConfig3 {

    public enum ConfigEnum {
        LifeEnvironment,
        StaticBall,
        StaticBallWithField,
        StaticBallWithPotential,
        StaticBallWithPotentialAndField,
        MovingBallWithField,
        MovingBallWithPotential,
        MovingBallWithPotentialAndField,
        InteractingBallsWithFieldsNP,
        InteractingBallsWithFieldsNN,
        InteractingBallsWithPotentialAndFieldsNN,
        InteractingBallsWithPotentialAndFieldsNP,

        CollideBallsWithPotentialAndFieldsNN,
    }

    public static boolean useGridNodeAreaRef = false;

    public static int HexGridXSize = 12;//10;
    public static int HexGridYSize = 3;


    public static int MaxPotentialProbability = 6 * 6 * 6;
    public static int MaxImpulsePercent = 100;
    public static int MaxImpulseProb = 6 * 6 * 6;
    //public static int MaxProb = Integer.MAX_VALUE / 6 / 6;
    public static int FieldPotentialCutoffValue = 6;
    public static final int InitialFieldPartProbabilityFactor = 6;

    public static ConfigEnum config;
    public static boolean useBall = false;
    public static boolean useBallPush = false;

    public static final int InitialBallPartMass = 8;
    public static final double InitialBallPartEnergy = 1.0D / 2.0D;
    public static final int InitialBallPartPotentialProbability = MaxPotentialProbability;

    public static final int InitialWallPartMass = 0;
    public static final double InitialWallPartEnergy = 0.0D;
    public static final int InitialWallPartProbability = 100;
    //public static final int InitialFieldPartProbabilityFactor = 6;


    public static boolean useRotation = false;

    public static boolean UseWalls = false;
    public static boolean UseExtraWalls = false;


    public static int[] BallStartXPos;
    public static int[] BallStartYPos;
    public static StartVelocity[] BallStartVelocity;
    public static Particle.PartSubType[] BallPartSubTypeArr;
    public static Particle.PartSubType[] BallFieldSubTypeArr;

    private MainConfig3() {
    }

    public enum StartVelocity {
        Static,
        StaticPotential,
        MovingRight,
        MovingRightWithPotential,
        MovingLeft,
        MovingLeftWithPotential,
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
                BallStartVelocity = new StartVelocity[] { Static };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.Nothing };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case StaticBallWithField -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocity = new StartVelocity[] { Static };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case StaticBallWithPotential -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocity = new StartVelocity[] { StaticPotential };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.Nothing };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case StaticBallWithPotentialAndField -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocity = new StartVelocity[] { StaticPotential };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case MovingBallWithField -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocity = new StartVelocity[] { MovingRight };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case MovingBallWithPotential -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocity = new StartVelocity[] { MovingRightWithPotential };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.Nothing };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case MovingBallWithPotentialAndField -> {
                useBall = true;
                BallStartXPos = new int[] { 42 };
                BallStartYPos = new int[] { 22 };
                BallStartVelocity = new StartVelocity[] { MovingRightWithPotential };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case InteractingBallsWithFieldsNP -> {
                useBall = true;
                BallStartXPos = new int[] { 42 - 3, 42 + 3 };
                BallStartYPos = new int[] { 22, 22 };
                BallStartVelocity = new StartVelocity[] { Static, Static };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE, Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN, Particle.PartSubType.FieldP };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case InteractingBallsWithFieldsNN -> {
                useBall = true;
                BallStartXPos = new int[] { 42 - 3, 42 + 3 };
                BallStartYPos = new int[] { 22, 22 };
                BallStartVelocity = new StartVelocity[] { Static, Static };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE, Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN, Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case InteractingBallsWithPotentialAndFieldsNN -> {
                useBall = true;
                BallStartXPos = new int[] { 42 - 3, 42 + 3 };
                BallStartYPos = new int[] { 22, 22 };
                BallStartVelocity = new StartVelocity[] { StaticPotential, StaticPotential };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE, Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN, Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case InteractingBallsWithPotentialAndFieldsNP -> {
                useBall = true;
                BallStartXPos = new int[] { 42 - 3, 42 + 3 };
                BallStartYPos = new int[] { 22, 22 };
                BallStartVelocity = new StartVelocity[] { StaticPotential, StaticPotential };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE, Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN, Particle.PartSubType.FieldP };
                UseWalls = false;
                UseExtraWalls = false;
            }
            case CollideBallsWithPotentialAndFieldsNN -> {
                useBall = true;
                BallStartXPos = new int[] { 42 - 8, 42 + 8 };
                BallStartYPos = new int[] { 22, 22 };
                BallStartVelocity = new StartVelocity[] { MovingRightWithPotential, MovingLeftWithPotential };
                BallPartSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.ParticleE, Particle.PartSubType.ParticleE };
                BallFieldSubTypeArr = new Particle.PartSubType[] { Particle.PartSubType.FieldN, Particle.PartSubType.FieldN };
                UseWalls = false;
                UseExtraWalls = false;
            }
        }
    }

}
