package de.schmiereck.hexWave.utils;

import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.math.HexVector;
import de.schmiereck.hexWave.math.MoveHexVector;
import de.schmiereck.hexWave.service.hexGrid.Cell;

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
        Cell.Dir lastDir = moveHexVector.lastDir;

        for (int tryPos = 0; tryPos < Cell.Dir.values().length; tryPos++) {
            switch (lastDir) {
                case AP -> {
                    if (moveHexVector.b > MaxMoveValue) {
                        moveDir = Cell.Dir.BP;
                    } else {
                        lastDir = Cell.Dir.BP;
                    }
                }
                case BP -> {
                    if (moveHexVector.c < -MaxMoveValue) {
                        moveDir = Cell.Dir.CN;
                    } else {
                        lastDir = Cell.Dir.CN;
                    }
                }
                case CN -> {
                    if (moveHexVector.a < -MaxMoveValue) {
                        moveDir = Cell.Dir.AN;
                    } else {
                        lastDir = Cell.Dir.AN;
                    }
                }
                case AN -> {
                    if (moveHexVector.b < -MaxMoveValue) {
                        moveDir = Cell.Dir.BN;
                    } else {
                        lastDir = Cell.Dir.BN;
                    }
                }
                case BN -> {
                    if (moveHexVector.c > MaxMoveValue) {
                        moveDir = Cell.Dir.CP;
                    } else {
                        lastDir = Cell.Dir.CP;
                    }
                }
                case CP -> {
                    if (moveHexVector.a > MaxMoveValue) {
                        moveDir = Cell.Dir.AP;
                    } else {
                        lastDir = Cell.Dir.AP;
                    }
                }
                default -> throw new IllegalStateException("Unexpected dir value: " + lastDir);
            }
            if (Objects.nonNull(moveDir)) {
                break;
            }
        }
        return moveDir;
    }

    public static void calcNextMove(final Cell.Dir moveDir, final MoveHexVector moveHexVector) {
        switch (moveDir) {
            case BP -> {
                moveHexVector.b -= MaxMoveValue;
                moveHexVector.lastDir = Cell.Dir.BP;
            }
            case CN -> {
                moveHexVector.c += MaxMoveValue;
                moveHexVector.lastDir = Cell.Dir.CN;
            }
            case AN -> {
                moveHexVector.a += MaxMoveValue;
                moveHexVector.lastDir = Cell.Dir.AN;
            }
            case BN -> {
                moveHexVector.b += MaxMoveValue;
                moveHexVector.lastDir = Cell.Dir.BN;
            }
            case CP -> {
                moveHexVector.c -= MaxMoveValue;
                moveHexVector.lastDir = Cell.Dir.CP;
            }
            case AP -> {
                moveHexVector.a -= MaxMoveValue;
                moveHexVector.lastDir = Cell.Dir.AP;
            }
            default -> throw new IllegalStateException("Unexpected dir value: " + moveDir);
        }
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
    }

    public static void calcElasticCollisionWithSolidWall(final HexParticle a1HexParticle, final Cell.Dir moveDir) {
        final int v1 = calcGetVelocity(a1HexParticle, moveDir);

        calcSetVelocity(a1HexParticle, moveDir,  -v1);
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
}
