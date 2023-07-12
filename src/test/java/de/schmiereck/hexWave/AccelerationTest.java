package de.schmiereck.hexWave;

import org.junit.jupiter.api.Test;

public class AccelerationTest {
    @Test
    public void acceleration() {
        int xPos = 10;
        int xAMove = 0;
        int xVMove = 0;
        int xOutAcceleration = 0;
        int xVelocity = 0;

        //final int MaxMove = 64;
        final int MaxMove = 60;
        final int MaxVelocity = MaxMove * MaxMove;

        final int xGravitation = -10;
        //final int xGravitation = 10;

        //final int xGravitation = -15;
        //final int xGravitation = 15;

        //final int xGravitation = -19;
        //final int xGravitation = 19;

        for (int cnt = 0; cnt < 300; cnt++) {
            xOutAcceleration = xGravitation;

            //xAMove += calcSaveFract(xOutAcceleration, xVelocity, MaxMove);
            xAMove += xOutAcceleration;

            final int moveDir = calcMoveDir(xAMove, xVMove, MaxMove);
            //final int moveDir = calcMoveDir2(xAMove, xVMove, MaxMove);
            // Will Move?
            if (moveDir != 0) {
                // Blocked?
                if (((xPos + moveDir) == 20) || ((xPos + moveDir) == 0)) {
                    System.out.printf("xPos:%2d, xAMove:%4d, xVMove:%4d, xVelocity:%4d\t <- HIT-BEVOR  ----\n", xPos, xAMove, xVMove, xVelocity);
                    //xVMove -= ((MaxMove + MaxMove) * moveDir);
                    xVelocity = -xVelocity;
                    //xVMove += xVelocity - ((MaxMove + MaxMove) * -moveDir); // Korrektur-Hack!!!! //xVMove += 30;  für moveDir = -1
                    xVMove += xVelocity; // Korrektur-Hack!!!! //xVMove += 30;  für moveDir = -1
                    System.out.printf("xPos:%2d, xAMove:%4d, xVMove:%4d, xVelocity:%4d\t <- HIT-AFTER  ----\n", xPos, xAMove, xVMove, xVelocity);
                } else {
                    // Move:
                    System.out.printf("xPos:%2d, xAMove:%4d, xVMove:%4d, xVelocity:%4d\t <- MOVE-BEVOR\n", xPos, xAMove, xVMove, xVelocity);

                    //xVelocity += xAMove;
                    xVelocity = calcNewVelocity(xVelocity, xAMove, MaxVelocity);
                    xAMove = 0;

                    xVMove -= (MaxMove + MaxMove) * moveDir;
                    //xVMove -= (MaxMove) * moveDir;

                    xPos += moveDir;
                    System.out.printf("xPos:%2d, xAMove:%4d, xVMove:%4d, xVelocity:%4d\t <- MOVE-AFTER\n", xPos, xAMove, xVMove, xVelocity);
                }
            } else {
                System.out.printf("xPos:%2d, xAMove:%4d, xVMove:%4d, xVelocity:%4d\t <- STAY-BEVORE\n", xPos, xAMove, xVMove, xVelocity);
                System.out.printf("xPos:%2d, xAMove:%4d, xVMove:%4d, xVelocity:%4d\t <- STAY-AFTER\n", xPos, xAMove, xVMove, xVelocity);
            }

            //xVMove += xVelocity + xAMove;
            xVMove += xVelocity + calcNewAcceleration(xVelocity, xAMove, MaxVelocity);
        }
    }

    private int calcNewVelocity0(final int xVelocity, final int xAMove, final int MaxVelocity) {
        return xVelocity + xAMove;
    }

    private int calcNewVelocity1(final int xVelocity, final int xAMove, final int MaxVelocity) {
        // m(v) = m0 * (c / (c - v))
        int d = ((MaxVelocity - xVelocity) * xAMove) / MaxVelocity;
        return xVelocity + d;
    }

    @Test
    public void testCalcNewVelocity() {
        final int MaxMove = 10;
        final int MaxVelocity = MaxMove * MaxMove;

        for (int v = -MaxVelocity * 2; v < MaxVelocity * 2; v += 5) {
            int xAMove = 10;
            int nv = calcNewVelocity(v, xAMove, MaxVelocity);
            System.out.printf("nv:%4d, v:%4d, xAMove:%4d\n", nv, v, xAMove);
        }
    }

    private static int calcNewAcceleration(final int xVelocity, final int xAMove, final int MaxVelocity) {
        // m(v) = m0 / (sqrt(1 - (v² / c²)))
        // m(v) = m0 * (c / (c - v))
        //int nv = xVelocity + xAMove;
        //int nm = Math.ceilDiv((MaxVelocity - nv) * xAMove, MaxVelocity);

        //int nm = Math.ceilDiv(((MaxVelocity - Math.abs(xVelocity)) * xAMove), MaxVelocity);
        int nm = (int)Math.floorDiv((long)((MaxVelocity - Math.abs(xVelocity)) * xAMove), (long)MaxVelocity);

        return nm;
    }

    private static int calcNewVelocity(final int xVelocity, final int xAMove, final int MaxVelocity) {
        int nm = calcNewAcceleration(xVelocity, xAMove, MaxVelocity);

        return calcMinMax(xVelocity + nm, MaxVelocity);
    }

    @Test
    public void testMinMax() {
        final int MaxMove = 10;
        final int MaxVelocity = MaxMove * MaxMove;

        for (int v = -MaxVelocity * 2; v < MaxVelocity * 2; v += 10) {
            int nv = calcMinMax(v, MaxVelocity);
            System.out.printf("nv:%4d, v:%4d\n", nv, v);
        }
    }

    private static int calcMinMax(final int v, final int max) {
        return Math.min(Math.max(v, -max), max);
    }

    private int calcSaveFract(int xOutAcceleration, int xVelocity, final int MaxMove) {
        final int v = (Math.abs(xVelocity) / MaxMove);
        if (v == 0)
            return xOutAcceleration;
        else
            return xOutAcceleration / v;
    }

    private static int calcMoveDir(int xAMove, int xVMove, int MaxMove) {
        final int moveDir;
        if ((xAMove + xVMove) >= MaxMove) {
            moveDir = 1;
        } else {
            if ((xAMove + xVMove) <= -MaxMove) {
                moveDir = -1;
            } else {
                moveDir = 0;
            }
        }
        return moveDir;
    }

    private static int calcMoveDir2(int xAMove, int xVMove, int MaxMove) {
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
}
