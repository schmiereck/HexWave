package de.schmiereck.hexWave.utils;

import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.math.HexVector;
import de.schmiereck.hexWave.math.MoveHexVector;
import de.schmiereck.hexWave.service.hexGrid.Cell;

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

    public static void transferVelocityToMove(final HexVector velocityHexVector, final MoveHexVector moveHexVector) {
        moveHexVector.a += velocityHexVector.a;
        moveHexVector.b += velocityHexVector.b;
        moveHexVector.c += velocityHexVector.c;
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


    public static List<Cell.Dir> determineNextMoveList(final MoveHexVector moveHexVector) {
        List<Cell.Dir> moveDirList = new ArrayList<>();
        Cell.Dir nextCheckedDir = moveHexVector.lastCheckedDir;

        for (int tryDirSpinPos = 0; tryDirSpinPos < (Cell.Dir.values().length); tryDirSpinPos++) {
            nextCheckedDir = calcNextMoveDir(nextCheckedDir);
            switch (nextCheckedDir) {
                case AP -> {
                    if (moveHexVector.a >= MaxMoveValue) {
                        moveDirList.add(Cell.Dir.AP);
                    }
                }
                case BP -> {
                    if (moveHexVector.b >= MaxMoveValue) {
                        moveDirList.add(Cell.Dir.BP);
                    }
                }
                case CN -> {
                    if (moveHexVector.c <= -MaxMoveValue) {
                        moveDirList.add(Cell.Dir.CN);
                    }
                }
                case AN -> {
                    if (moveHexVector.a <= -MaxMoveValue) {
                        moveDirList.add(Cell.Dir.AN);
                    }
                }
                case BN -> {
                    if (moveHexVector.b <= -MaxMoveValue) {
                        moveDirList.add(Cell.Dir.BN);
                    }
                }
                case CP -> {
                    if (moveHexVector.c >= MaxMoveValue) {
                        moveDirList.add(Cell.Dir.CP);
                    }
                }
                default -> throw new IllegalStateException("Unexpected dir value: " + nextCheckedDir);
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
            case AN -> moveHexVector.a += (MaxMoveValue);
            case AP -> moveHexVector.a -= (MaxMoveValue);
            case BN -> moveHexVector.b += (MaxMoveValue);
            case BP -> moveHexVector.b -= (MaxMoveValue);
            case CN -> moveHexVector.c += (MaxMoveValue);
            case CP -> moveHexVector.c -= (MaxMoveValue);
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

    public static void calcNextMove2(final Cell.Dir moveDir, final MoveHexVector moveHexVector, final HexVector velocityHexVector) {
        switch (moveDir) {
            case AN -> moveHexVector.a += (MaxMoveValue + (MaxMoveValue - velocityHexVector.a));
            case AP -> moveHexVector.a -= (MaxMoveValue + (MaxMoveValue - velocityHexVector.a));
            case BN -> moveHexVector.b += (MaxMoveValue + (MaxMoveValue - velocityHexVector.b));
            case BP -> moveHexVector.b -= (MaxMoveValue + (MaxMoveValue - velocityHexVector.b));
            case CN -> moveHexVector.c += (MaxMoveValue + (MaxMoveValue - velocityHexVector.c));
            case CP -> moveHexVector.c -= (MaxMoveValue + (MaxMoveValue - velocityHexVector.c));
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

    public static void calcNextMove3(final Cell.Dir moveDir, final MoveHexVector moveHexVector, final HexVector velocityHexVector) {
        switch (moveDir) {
            case AN -> moveHexVector.a += (MaxMoveValue + (MaxMoveValue));
            case AP -> moveHexVector.a -= (MaxMoveValue + (MaxMoveValue));
            case BN -> moveHexVector.b += (MaxMoveValue + (MaxMoveValue));
            case BP -> moveHexVector.b -= (MaxMoveValue + (MaxMoveValue));
            case CN -> moveHexVector.c += (MaxMoveValue + (MaxMoveValue));
            case CP -> moveHexVector.c -= (MaxMoveValue + (MaxMoveValue));
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

   public static Cell.Dir calcNextMoveDir(final Cell.Dir moveDir) {
       final Cell.Dir nextMoveDir = switch (moveDir) {
            case BP -> Cell.Dir.CN;
            case CN -> Cell.Dir.AN;
            case AN -> Cell.Dir.BN;
            case BN -> Cell.Dir.CP;
            case CP -> Cell.Dir.AP;
            case AP -> Cell.Dir.BP;
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
/*
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

 */

    private static int calcCollision1to2(int v1, int m1, int v2, int m2) {
        return (m1 * v1 + m2 * (2 * v2 - v1)) / (m1 + m2);
    }

    public static void calcElasticCollisionWithSolidWall(final HexParticle hexParticle, final Cell.Dir moveDir) {
        final int v1 = calcGetVelocity(hexParticle, moveDir);
        final int m1 = calcGetMove(hexParticle, moveDir);

        calcSetVelocity(hexParticle, moveDir,  -v1);
        calcSetMove(hexParticle, moveDir,  -m1);
    }

    public static void calcVelocityElasticCollisionWithSolidWall2(final HexParticle hexParticle, final Cell.Dir moveDir) {
        final int v1 = calcGetVelocity(hexParticle, moveDir);
        //final int m1 = calcGetMove(hexParticle, moveDir);

        calcSetVelocity(hexParticle, moveDir,  -v1);
        //calcSetMove(hexParticle, moveDir,  -m1);
    }
/*
    public static void calcAccelerationElasticCollisionWithSolidWall2(final HexParticle hexParticle, final Cell.Dir moveDir) {
        final int v1 = calcGetOutAcceleration(hexParticle, moveDir);
        //final int m1 = calcGetMove(hexParticle, moveDir);

        calcAddInAcceleration(hexParticle, moveDir,  -v1);
        //calcSetMove(hexParticle, moveDir,  -m1);

        calcSetOutAcceleration(hexParticle, moveDir,  0);
    }

 */
/*
    public static void runAccelerationAddInToOut(HexParticle hexParticle) {
        hexParticle.getOutAccelerationHexVector().a += hexParticle.getInAccelerationHexVector().a;
        hexParticle.getOutAccelerationHexVector().b += hexParticle.getInAccelerationHexVector().b;
        hexParticle.getOutAccelerationHexVector().c += hexParticle.getInAccelerationHexVector().c;

        calcClearHexVector(hexParticle.getInAccelerationHexVector());
    }

 */

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
/*
    private static int calcGetInAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir) {
        return calcGetHexVector(hexParticle.getInAccelerationHexVector(), moveDir);
    }

    private static int calcGetOutAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir) {
        return calcGetHexVector(hexParticle.getOutAccelerationHexVector(), moveDir);
    }

 */

    private static int calcGetHexVector(final HexVector hexVector, final Cell.Dir moveDir) {
        final int velocity;
        switch (moveDir) {
            case BP -> velocity = hexVector.b;
            case CN -> velocity = hexVector.c;
            case AN -> velocity = hexVector.a;
            case BN -> velocity = hexVector.b;
            case CP -> velocity = hexVector.c;
            case AP -> velocity = hexVector.a;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
        return velocity;
    }

    private static void calcSetVelocity(final HexParticle hexParticle, final Cell.Dir moveDir, final int velocity) {
        calcSetHexVector(hexParticle.getVelocityHexVector(), moveDir, velocity);
    }
/*
    private static void calcSetInAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir, final int inAcceleration) {
        calcSetHexVector(hexParticle.getInAccelerationHexVector(), moveDir, inAcceleration);
    }

    private static void calcAddInAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir, final int inAcceleration) {
        calcAddHexVector(hexParticle.getInAccelerationHexVector(), moveDir, inAcceleration);
    }

    private static void calcSetOutAcceleration(final HexParticle hexParticle, final Cell.Dir moveDir, final int outAcceleration) {
        calcSetHexVector(hexParticle.getOutAccelerationHexVector(), moveDir, outAcceleration);
    }

 */

    private static void calcSetHexVector(final HexVector hexVector, final Cell.Dir moveDir, final int value) {
        switch (moveDir) {
            case BP -> hexVector.b = value;
            case CN -> hexVector.c = value;
            case AN -> hexVector.a = value;
            case BN -> hexVector.b = value;
            case CP -> hexVector.c = value;
            case AP -> hexVector.a = value;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

    private static void calcAddHexVector(final HexVector hexVector, final Cell.Dir moveDir, final int value) {
        switch (moveDir) {
            case BP -> hexVector.b = value;
            case CN -> hexVector.c = value;
            case AN -> hexVector.a = value;
            case BN -> hexVector.b = value;
            case CP -> hexVector.c = value;
            case AP -> hexVector.a = value;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
    }

    public static int calcAddVelocity(final HexParticle hexParticle, final Cell.Dir moveDir, final int velocity) {
        switch (moveDir) {
            case AP -> hexParticle.getVelocityHexVector().a += velocity;
            case AN -> hexParticle.getVelocityHexVector().a -= velocity;
            case BP -> hexParticle.getVelocityHexVector().b += velocity;
            case BN -> hexParticle.getVelocityHexVector().b -= velocity;
            case CP -> hexParticle.getVelocityHexVector().c += velocity;
            case CN -> hexParticle.getVelocityHexVector().c -= velocity;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
        return velocity;
    }

    private static int calcGetMove(final HexParticle hexParticle, final Cell.Dir moveDir) {
        final int move;
        switch (moveDir) {
            case BP -> move = hexParticle.getMoveHexVector().b;
            case CN -> move = hexParticle.getMoveHexVector().c;
            case AN -> move = hexParticle.getMoveHexVector().a;
            case BN -> move = hexParticle.getMoveHexVector().b;
            case CP -> move = hexParticle.getMoveHexVector().c;
            case AP -> move = hexParticle.getMoveHexVector().a;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
        return move;
    }

    private static int calcSetMove(final HexParticle hexParticle, final Cell.Dir moveDir, final int move) {
        switch (moveDir) {
            case BP -> hexParticle.getMoveHexVector().b = move;
            case CN -> hexParticle.getMoveHexVector().c = move;
            case AN -> hexParticle.getMoveHexVector().a = move;
            case BN -> hexParticle.getMoveHexVector().b = move;
            case CP -> hexParticle.getMoveHexVector().c = move;
            case AP -> hexParticle.getMoveHexVector().a = move;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
        return move;
    }
}
