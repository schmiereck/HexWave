package de.schmiereck.hexWave2.utils;

import de.schmiereck.hexWave2.math.HexParticle;
import de.schmiereck.hexWave2.math.HexVector;
import de.schmiereck.hexWave2.math.MoveHexVector;
import de.schmiereck.hexWave2.service.hexGrid.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
    bn  cp
     \ /
 an---A---ap
     / \
    cn  bp
 *
 */
public class HexMathUtils {
    public static final int MaxMoveValue = 256;
    public static final Cell.Dir[] MoveDirArr = {
            Cell.Dir.AP, Cell.Dir.BP, Cell.Dir.CN, Cell.Dir.AN, Cell.Dir.BN, Cell.Dir.CP
    };

    private HexMathUtils() { }

    public static void transferVelocityToMove(final HexParticle hexParticle) {
        final HexVector velocityHexVector = hexParticle.getVelocityHexVector();
        final MoveHexVector vMoveHexVector = hexParticle.getVMoveHexVector();

        vMoveHexVector.a += (velocityHexVector.a);
        vMoveHexVector.b += (velocityHexVector.b);
        vMoveHexVector.c += (velocityHexVector.c);
    }

    public static void transferAcceleratioToMove(final HexParticle hexParticle) {
        final HexVector outAcceleratioHexVector = hexParticle.getOutAccelerationHexVector();
        final MoveHexVector aMoveHexVector = hexParticle.getAMoveHexVector();

        aMoveHexVector.a += (outAcceleratioHexVector.a);
        aMoveHexVector.b += (outAcceleratioHexVector.b);
        aMoveHexVector.c += (outAcceleratioHexVector.c);
    }

    public static Cell.Dir determineNextMove(final MoveHexVector moveHexVector) {
        Cell.Dir moveDir = null;
        Cell.Dir lastDir = moveHexVector.lastCheckedDir;

        for (int tryPos = 0; tryPos < (Cell.Dir.values().length - 1); tryPos++) {
            final Cell.Dir nextCheckedDir = calcNextMoveDir(moveHexVector.lastCheckedDir);
            switch (nextCheckedDir) {
                case AP -> {
                    if (moveHexVector.a >= MaxMoveValue) {
                        moveDir = Cell.Dir.AP;
                    }
                }
                case BP -> {
                    if (moveHexVector.b >= MaxMoveValue) {
                        moveDir = Cell.Dir.BP;
                    }
                }
                case CN -> {
                    if (moveHexVector.c <= -MaxMoveValue) {
                        moveDir = Cell.Dir.CN;
                    }
                }
                case AN -> {
                    if (moveHexVector.a <= -MaxMoveValue) {
                        moveDir = Cell.Dir.AN;
                    }
                }
                case BN -> {
                    if (moveHexVector.b <= -MaxMoveValue) {
                        moveDir = Cell.Dir.BN;
                    }
                }
                case CP -> {
                    if (moveHexVector.c >= MaxMoveValue) {
                        moveDir = Cell.Dir.CP;
                    }
                }
                default -> throw new IllegalStateException("Unexpected dir value: " + lastDir);
            }
            moveHexVector.lastCheckedDir = nextCheckedDir;
            if (Objects.nonNull(moveDir)) {
                break;
            }
        }
        return moveDir;
    }

    public static List<Cell.Dir> determineNextMoveList(final HexParticle hexParticle) {
        List<Cell.Dir> moveDirList = new ArrayList<>();
        final MoveHexVector moveHexVector = hexParticle.getVMoveHexVector();
        Cell.Dir nextCheckedDir = moveHexVector.lastCheckedDir;

        for (int tryDirSpinPos = 0; tryDirSpinPos < (Cell.Dir.values().length); tryDirSpinPos++) {
            nextCheckedDir = calcNextMoveDir(nextCheckedDir);
            final int aMove = HexMathUtils.calcGetAMove(hexParticle, nextCheckedDir);
            final int vMove = HexMathUtils.calcGetVMove(hexParticle, nextCheckedDir);
            final int outAcceleration = HexMathUtils.calcGetOutAcceleration(hexParticle, nextCheckedDir);

            //if ((vMove + outAcceleration) >= MaxMoveValue) {
            //if ((aMove + vMove) >= MaxMoveValue) {
            if ((vMove) >= MaxMoveValue) {
            //if ((aMove) > MaxMoveValue) {
                moveDirList.add(nextCheckedDir);
            }
        }
        return moveDirList;
    }

    public static List<Cell.Dir> determineNextAMoveList(final HexParticle hexParticle) {
        List<Cell.Dir> moveDirList = new ArrayList<>();
        final MoveHexVector moveHexVector = hexParticle.getAMoveHexVector();
        Cell.Dir nextCheckedDir = moveHexVector.lastCheckedDir;

        for (int tryDirSpinPos = 0; tryDirSpinPos < (Cell.Dir.values().length); tryDirSpinPos++) {
            nextCheckedDir = calcNextMoveDir(nextCheckedDir);
            final int aMove = HexMathUtils.calcGetAMove(hexParticle, nextCheckedDir);

            if ((aMove) >= MaxMoveValue) {
                moveHexVector.lastCheckedDir = nextCheckedDir;
                moveDirList.add(nextCheckedDir);
            }
        }
        return moveDirList;
    }

    /**
        bn  cp
         \ /
     an---A---ap
         / \
        cn  bp
     *
     */
    public static void calcNextMove(final Cell.Dir moveDir, final MoveHexVector moveHexVector, final HexVector velocityHexVector) {
        switch (moveDir) {
            case AP -> moveHexVector.a -= (MaxMoveValue);
            case AN -> moveHexVector.a += (MaxMoveValue);
            case BP -> moveHexVector.b -= (MaxMoveValue);
            case BN -> moveHexVector.b += (MaxMoveValue);
            case CP -> moveHexVector.c -= (MaxMoveValue);
            case CN -> moveHexVector.c += (MaxMoveValue);
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

    public static void calcNextMove2(final Cell.Dir moveDir, final MoveHexVector moveHexVector, final HexVector velocityHexVector) {
        switch (moveDir) {
            case AP -> moveHexVector.a -= (MaxMoveValue + (MaxMoveValue - -velocityHexVector.a));
            case AN -> moveHexVector.a += (MaxMoveValue + (MaxMoveValue - velocityHexVector.a));
            case BP -> moveHexVector.b -= (MaxMoveValue + (MaxMoveValue - -velocityHexVector.b));
            case BN -> moveHexVector.b += (MaxMoveValue + (MaxMoveValue - velocityHexVector.b));
            case CP -> moveHexVector.c -= (MaxMoveValue + (MaxMoveValue - -velocityHexVector.c));
            case CN -> moveHexVector.c += (MaxMoveValue + (MaxMoveValue - velocityHexVector.c));
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

    public static void calcNextMove22(final Cell.Dir moveDir, final MoveHexVector moveHexVector, final HexVector velocityHexVector) {
        switch (moveDir) {
            case AP -> moveHexVector.a += (-velocityHexVector.a);
            case AN -> moveHexVector.a += (-velocityHexVector.a);
            case BP -> moveHexVector.b += (-velocityHexVector.b);
            case BN -> moveHexVector.b += (-velocityHexVector.b);
            case CP -> moveHexVector.c += (-velocityHexVector.c);
            case CN -> moveHexVector.c += (-velocityHexVector.c);
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

    public static void calcNextMove3(final Cell.Dir moveDir, final MoveHexVector moveHexVector, final HexVector velocityHexVector) {
        switch (moveDir) {
            case AP -> moveHexVector.a -= (MaxMoveValue + (MaxMoveValue));
            case AN -> moveHexVector.a += (MaxMoveValue + (MaxMoveValue));
            case BP -> moveHexVector.b -= (MaxMoveValue + (MaxMoveValue));
            case BN -> moveHexVector.b += (MaxMoveValue + (MaxMoveValue));
            case CP -> moveHexVector.c -= (MaxMoveValue + (MaxMoveValue));
            case CN -> moveHexVector.c += (MaxMoveValue + (MaxMoveValue));
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

   public static Cell.Dir calcNextMoveDir(final Cell.Dir moveDir) {
       final Cell.Dir nextMoveDir = switch (moveDir) {
           case AP -> Cell.Dir.BP;
           case BP -> Cell.Dir.CN;
           case CN -> Cell.Dir.AN;
           case AN -> Cell.Dir.BN;
           case BN -> Cell.Dir.CP;
           case CP -> Cell.Dir.AP;
        };
        return nextMoveDir;
    }

    /**
     * Central Elastic Collision:
     * v1' = (m1 * v1 + m2 * (2 * v2 - v1)) / (m1 + m2)
     * v2' = (m2 * v2 + m1 * (2 * v1 - v2)) / (m1 + m2)
     */
    public static void calcVelocityElasticCollision(final HexParticle a1HexParticle, final Cell.Dir moveDir, final HexParticle a2HexParticle) {
        final int v1 = calcGetVelocity(a1HexParticle, moveDir);
        final int m1 = a1HexParticle.getMass();
        final int v2 = calcGetVelocity(a2HexParticle, moveDir);
        final int m2 = a2HexParticle.getMass();

        calcSetVelocity(a1HexParticle, moveDir, calcCollision1to2(v1, m1, v2, m2));
        calcSetVelocity(a2HexParticle, moveDir, calcCollision1to2(v2, m2, v1, m1));
        // TODO ??? calcSetMove(a1HexParticle, moveDir,  (m1 * mo1 + m2 * (2 * mo2 - mo1)) / (m1 + m2)); ...
    }

    public static void calcAccelerationElasticCollision(final HexParticle a1HexParticle, final Cell.Dir moveDir, final HexParticle a2HexParticle) {
        final int v1 = calcGetOutAcceleration(a1HexParticle, moveDir);
        final int m1 = a1HexParticle.getMass();
        final int v2 = calcGetOutAcceleration(a2HexParticle, moveDir);
        final int m2 = a2HexParticle.getMass();

        calcAddInAcceleration(a1HexParticle, moveDir, calcCollision1to2(v1, m1, v2, m2));
        calcAddInAcceleration(a2HexParticle, moveDir, calcCollision1to2(v2, m2, v1, m1));
        // TODO ??? calcSetMove(a1HexParticle, moveDir,  (m1 * mo1 + m2 * (2 * mo2 - mo1)) / (m1 + m2)); ...

        calcSetOutAcceleration(a1HexParticle, moveDir,  0);
        calcSetOutAcceleration(a2HexParticle, moveDir,  0);
    }

    private static int calcCollision1to2(int v1, int m1, int v2, int m2) {
        return (m1 * v1 + m2 * (2 * v2 - v1)) / (m1 + m2);
    }

    public static void calcVelocityElasticCollisionWithSolidWall(final HexParticle hexParticle, final Cell.Dir moveDir) {
        final int v1 = calcGetVelocity(hexParticle, moveDir);
        final int m1 = calcGetVMove(hexParticle, moveDir);

        calcSetVelocity(hexParticle, moveDir,  -v1);
        calcSetVMove(hexParticle, moveDir,  -m1);
    }

    public static void calcVelocityElasticCollisionWithSolidWall2(final HexParticle hexParticle, final Cell.Dir moveDir) {
        final int v1 = calcGetVelocity(hexParticle, moveDir);
        //final int m1 = calcGetVMove(hexParticle, moveDir);

        calcSetVelocity(hexParticle, moveDir,  -v1);
        //calcSetVMove(hexParticle, moveDir,  -m1);
    }

    public static void calcAccelerationElasticCollisionWithSolidWall2(final HexParticle hexParticle, final Cell.Dir moveDir) {
        final int v1 = calcGetOutAcceleration(hexParticle, moveDir);
        //final int m1 = calcGetMove(hexParticle, moveDir);

        calcAddInAcceleration(hexParticle, moveDir,  -v1);
        //calcSetMove(hexParticle, moveDir,  -m1);

        calcSetOutAcceleration(hexParticle, moveDir,  0);
    }

    public static void runAccelerationSetInToOut(final HexParticle hexParticle) {
        hexParticle.getOutAccelerationHexVector().a = hexParticle.getInAccelerationHexVector().a;
        hexParticle.getOutAccelerationHexVector().b = hexParticle.getInAccelerationHexVector().b;
        hexParticle.getOutAccelerationHexVector().c = hexParticle.getInAccelerationHexVector().c;

        calcClearHexVector(hexParticle.getInAccelerationHexVector());
    }

    public static void runAccelerationAddInToOut(final HexParticle hexParticle) {
        hexParticle.getOutAccelerationHexVector().a += hexParticle.getInAccelerationHexVector().a;
        hexParticle.getOutAccelerationHexVector().b += hexParticle.getInAccelerationHexVector().b;
        hexParticle.getOutAccelerationHexVector().c += hexParticle.getInAccelerationHexVector().c;

        calcClearHexVector(hexParticle.getInAccelerationHexVector());
    }

    private static void calcClearHexVector(final HexVector hexVector) {
        hexVector.a = 0;
        hexVector.b = 0;
        hexVector.c = 0;
    }
/*
    public static void calcOutAccelerationToVelocity(final HexParticle hexParticle) {
        // TODO mass
        hexParticle.getVelocityHexVector().a += hexParticle.getOutAccelerationHexVector().a;
        hexParticle.getVelocityHexVector().b += hexParticle.getOutAccelerationHexVector().b;
        hexParticle.getVelocityHexVector().c += hexParticle.getOutAccelerationHexVector().c;

        calcClearHexVector(hexParticle.getOutAccelerationHexVector());
    }

 */

    private static int calcGetVelocity(final HexParticle hexParticle, final Cell.Dir moveDir) {
        return calcGetHexVector(hexParticle.getVelocityHexVector(), moveDir);
    }

    private static int calcGetInAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir) {
        return calcGetHexVector(hexParticle.getInAccelerationHexVector(), moveDir);
    }

    public static int calcGetOutAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir) {
        return calcGetHexVector(hexParticle.getOutAccelerationHexVector(), moveDir);
    }

    private static void calcSetVelocity(final HexParticle hexParticle, final Cell.Dir moveDir, final int velocity) {
        calcSetHexVector(hexParticle.getVelocityHexVector(), moveDir, velocity);
    }

    public static void calcAddVelocity(final HexParticle hexParticle, final Cell.Dir dir, final int velocity) {
        calcAddHexVector(hexParticle.getVelocityHexVector(), dir, velocity);
    }

    public static void calcSetInAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir, final int inAcceleration) {
        calcSetHexVector(hexParticle.getInAccelerationHexVector(), moveDir, inAcceleration);
    }

    public static void calcAddInAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir, final int inAcceleration) {
        calcAddHexVector(hexParticle.getInAccelerationHexVector(), moveDir, inAcceleration);
    }

    public static void calcAddOutAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir, final int inAcceleration) {
        calcAddHexVector(hexParticle.getOutAccelerationHexVector(), moveDir, inAcceleration);
    }

    public static void calcSetOutAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir, final int outAcceleration) {
        calcSetHexVector(hexParticle.getOutAccelerationHexVector(), moveDir, outAcceleration);
    }

    private static int calcGetVMove(final HexParticle hexParticle, final Cell.Dir moveDir) {
        return calcGetHexVector(hexParticle.getVMoveHexVector(), moveDir);
    }

    private static void calcSetVMove(final HexParticle hexParticle, final Cell.Dir moveDir, final int move) {
        calcSetHexVector(hexParticle.getVMoveHexVector(), moveDir, move);
    }

    private static void calcAddVMove(final HexParticle hexParticle, final Cell.Dir moveDir, final int move) {
        calcAddHexVector(hexParticle.getVMoveHexVector(), moveDir, move);
    }

    private static int calcGetAMove(final HexParticle hexParticle, final Cell.Dir moveDir) {
        return calcGetHexVector(hexParticle.getAMoveHexVector(), moveDir);
    }

    private static void calcSetAMove(final HexParticle hexParticle, final Cell.Dir moveDir, final int move) {
        calcSetHexVector(hexParticle.getAMoveHexVector(), moveDir, move);
    }

    public static void transferAMoveToVMove(HexParticle hexParticle, Cell.Dir nextMoveDir) {
        final int aMove = HexMathUtils.calcGetAMove(hexParticle, nextMoveDir);
        //if (outAcceleration > 0)
        {
            HexMathUtils.calcAddVMove(hexParticle, nextMoveDir, aMove);
            HexMathUtils.calcSetAMove(hexParticle, nextMoveDir, 0);
        }
    }

    public static void transferVMoveToVelocity(HexParticle hexParticle, Cell.Dir nextMoveDir) {
        final int vMove = HexMathUtils.calcGetVMove(hexParticle, nextMoveDir);

        final int velocity = vMove;// / hexParticle.getMass();
        HexMathUtils.calcAddVelocity(hexParticle, nextMoveDir, velocity);
    }

    public static void clearAMove(HexParticle hexParticle, Cell.Dir nextMoveDir) {
        HexMathUtils.calcSetAMove(hexParticle, nextMoveDir, 0);
    }

    private static int calcGetHexVector(final HexVector hexVector, final Cell.Dir moveDir) {
        final int value;
        switch (moveDir) {
            case AP -> value = hexVector.a;
            case AN -> value = -hexVector.a;
            case BP -> value = hexVector.b;
            case BN -> value = -hexVector.b;
            case CP -> value = hexVector.c;
            case CN -> value = -hexVector.c;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
        return value;
    }

    private static void calcSetHexVector(final HexVector hexVector, final Cell.Dir moveDir, final int value) {
        switch (moveDir) {
            case AP -> hexVector.a = value;
            case AN -> hexVector.a = -value;
            case BP -> hexVector.b = value;
            case BN -> hexVector.b = -value;
            case CP -> hexVector.c = value;
            case CN -> hexVector.c = -value;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

    private static void calcAddHexVector(final HexVector hexVector, final Cell.Dir moveDir, final int value) {
        switch (moveDir) {
            case AP -> hexVector.a += value;
            case AN -> hexVector.a -= value;
            case BP -> hexVector.b += value;
            case BN -> hexVector.b -= value;
            case CP -> hexVector.c += value;
            case CN -> hexVector.c -= value;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }
}
