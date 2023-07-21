package de.schmiereck.hexWave;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class Acceleration4Test {
    @Test
    public void acceleration() {
        int xPos = 10;
        int xVMove = 0;
        int xOutAcceleration = 0;
        int xVelocity = 0;

        //final int MaxMove = 64;
        final int MaxMove = 60;
        final int MaxVelocity = MaxMove + MaxMove;

        final int xGravitation = -10;
        //final int xGravitation = 10;

        //final int xGravitation = -15;
        //final int xGravitation = 15;

        //final int xGravitation = -19;
        //final int xGravitation = 19;

        for (int cnt = 0; cnt < 300; cnt++) {
            xOutAcceleration = xGravitation;

            // Velocity kann größer als MaxVelocity werden und dann summieren sich die moves auf ohne abgebaut werden zu können!
            //int newXVelocity = xVelocity + xOutAcceleration;
            //int newXVelocity = xVelocity + calcNewAcceleration(xVelocity, xOutAcceleration, MaxMove);
            int newXVelocity = xVelocity + calcNewAcceleration(xVelocity, xOutAcceleration, MaxVelocity);

            final int velocityDir = calcVelocityDir(newXVelocity);
            if (velocityDir != 0) {
                if (checkBlocked(xPos, velocityDir)) {
                    // Give Acceleration to blocking Part.
                    //newXVelocity = xVelocity;
                } else {
                }
            }

            final int moveDir = calcMoveDir(xVMove, MaxMove);
            // Will Move?
            if (moveDir != 0) {
                // Blocked?
                if (checkBlocked(xPos, moveDir)) {
                    System.out.printf("xPos:%2d, xVMove:%4d, xVelocity:%4d\t <- HIT-BEVOR  ----\n", xPos, xVMove, xVelocity);
                    //xVelocity = -xVelocity;
                    xVelocity = -calcVelocity(xVelocity);
                    //xVMove -= (MaxMove + MaxMove) * moveDir;
                    xVMove = -xVMove;
                    xVMove -= (MaxMove + MaxMove) * moveDir;
                    //xVMove += (MaxMove) * moveDir;
                    //xVMove = calcVMove(xVMove, newXVelocity, MaxVelocity);
                    System.out.printf("xPos:%2d, xVMove:%4d, xVelocity:%4d\t <- HIT-AFTER  ----\n", xPos, xVMove, xVelocity);
                } else {
                    // Move:
                    System.out.printf("xPos:%2d, xVMove:%4d, xVelocity:%4d\t <- MOVE-BEVOR\n", xPos, xVMove, xVelocity);
                    xVMove = calcVMove(xVMove, newXVelocity, MaxVelocity);
                    xVelocity = calcVelocity(newXVelocity);
                    //xVelocity = calcNewVelocity(xVelocity, xAMove, MaxVelocity);

                    xVMove -= (MaxMove + MaxMove) * moveDir;

                    xPos += moveDir;
                    System.out.printf("xPos:%2d, xVMove:%4d, xVelocity:%4d\t <- MOVE-AFTER\n", xPos, xVMove, xVelocity);
                }
            } else {
                System.out.printf("xPos:%2d, xVMove:%4d, xVelocity:%4d\t <- STAY-BEVORE\n", xPos, xVMove, xVelocity);
                xVMove = calcVMove(xVMove, newXVelocity, MaxVelocity);
                xVelocity = calcVelocity(newXVelocity);
                System.out.printf("xPos:%2d, xVMove:%4d, xVelocity:%4d\t <- STAY-AFTER\n", xPos, xVMove, xVelocity);
            }
        }
    }

    private int calcVelocity(int newXVelocity) {
        return newXVelocity;
    }

    private static boolean checkBlocked(int xPos, int moveDir) {
        return ((xPos + moveDir) == 20) || ((xPos + moveDir) == 0);
    }

    private static int calcVMove(int xVMove, int xVelocity, int MaxVelocity) {
        return xVMove + xVelocity;
    }

    private static int calcNewAcceleration(final int xVelocity, final int xAcceleration, final int MaxVelocity) {
        final int dir = (xAcceleration == 0 ? 0 : (xAcceleration > 0 ? 1 : -1));
        final int newVelocity =  calcMinMax(xVelocity + xAcceleration, MaxVelocity);
        final int newVelocityDiff = xVelocity - newVelocity;
        final int newAcceleration =  calcMinMax(xAcceleration, MaxVelocity);

        int na;
        //na = Math.ceilDiv(((MaxVelocity - Math.abs(xVelocity)) * newAcceleration), MaxVelocity);
        //na = (int)Math.floorDiv((long)((MaxVelocity - Math.abs(xVelocity)) * newAcceleration), (long)MaxVelocity);
        //na = ((MaxVelocity - Math.abs(xVelocity)) * newAcceleration) / MaxVelocity;
        na = ((MaxVelocity - Math.abs(xVelocity)) * xAcceleration) / MaxVelocity;

        return na;
    }

    private static int calcMinMax(final int v, final int max) {
        return Math.min(Math.max(v, -max), max);
    }

    private static int calcMoveDir(final int xVMove, final int MaxMove) {
        final int moveDir;
        if ((xVMove) >= MaxMove) {
            moveDir = 1;
        } else {
            if ((xVMove) <= -MaxMove) {
                moveDir = -1;
            } else {
                moveDir = 0;
            }
        }
        return moveDir;
    }

    private static int calcVelocityDir(final int velocity) {
        final int velocityDir;
        if ((velocity) > 0) {
            velocityDir = 1;
        } else {
            if ((velocity) < 0) {
                velocityDir = -1;
            } else {
                velocityDir = 0;
            }
        }
        return velocityDir;
    }
}
