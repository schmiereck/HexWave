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
    public static void calcNextMove(final Cell.Dir moveDir, final MoveHexVector moveHexVector) {
        switch (moveDir) {
            case BP -> moveHexVector.b -= MaxMoveValue;
            case CN -> moveHexVector.c += MaxMoveValue;
            case AN -> moveHexVector.a += MaxMoveValue;
            case BN -> moveHexVector.b += MaxMoveValue;
            case CP -> moveHexVector.c -= MaxMoveValue;
            case AP -> moveHexVector.a -= MaxMoveValue;
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
    public static void calcElasticCollision(final HexParticle a1HexParticle, final Cell.Dir moveDir, final HexParticle a2HexParticle) {
        final int v1 = calcGetVelocity(a1HexParticle, moveDir);
        final int m1 = a1HexParticle.getMass();
        final int v2 = calcGetVelocity(a2HexParticle, moveDir);
        final int m2 = a2HexParticle.getMass();

        calcSetVelocity(a1HexParticle, moveDir,  (m1 * v1 + m2 * (2 * v2 - v1)) / (m1 + m2));
        calcSetVelocity(a2HexParticle, moveDir,  (m2 * v2 + m1 * (2 * v1 - v2)) / (m1 + m2));
        // TODO ??? calcSetMove(a1HexParticle, moveDir,  (m1 * mo1 + m2 * (2 * mo2 - mo1)) / (m1 + m2)); ...
    }

    public static void calcElasticCollisionWithSolidWall(final HexParticle a1HexParticle, final Cell.Dir moveDir) {
        final int v1 = calcGetVelocity(a1HexParticle, moveDir);
        final int m1 = calcGetMove(a1HexParticle, moveDir);

        calcSetVelocity(a1HexParticle, moveDir,  -v1);
        calcSetMove(a1HexParticle, moveDir,  -m1);
    }

    private static int calcGetVelocity(final HexParticle hexParticle, final Cell.Dir moveDir) {
        final int velocity;
        switch (moveDir) {
            case BP -> velocity = hexParticle.getVelocityHexVector().b;
            case CN -> velocity = hexParticle.getVelocityHexVector().c;
            case AN -> velocity = hexParticle.getVelocityHexVector().a;
            case BN -> velocity = hexParticle.getVelocityHexVector().b;
            case CP -> velocity = hexParticle.getVelocityHexVector().c;
            case AP -> velocity = hexParticle.getVelocityHexVector().a;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
        return velocity;
    }

    private static int calcSetVelocity(final HexParticle hexParticle, final Cell.Dir moveDir, final int velocity) {
        switch (moveDir) {
            case BP -> hexParticle.getVelocityHexVector().b = velocity;
            case CN -> hexParticle.getVelocityHexVector().c = velocity;
            case AN -> hexParticle.getVelocityHexVector().a = velocity;
            case BN -> hexParticle.getVelocityHexVector().b = velocity;
            case CP -> hexParticle.getVelocityHexVector().c = velocity;
            case AP -> hexParticle.getVelocityHexVector().a = velocity;
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
        return velocity;
    }

    private static int calcAddVelocity(final HexParticle hexParticle, final Cell.Dir moveDir, final int velocity) {
        switch (moveDir) {
            case BP -> hexParticle.getVelocityHexVector().b += velocity;
            case CN -> hexParticle.getVelocityHexVector().c += velocity;
            case AN -> hexParticle.getVelocityHexVector().a += velocity;
            case BN -> hexParticle.getVelocityHexVector().b += velocity;
            case CP -> hexParticle.getVelocityHexVector().c += velocity;
            case AP -> hexParticle.getVelocityHexVector().a += velocity;
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
