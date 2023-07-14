package de.schmiereck.hexWave;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

public class Acceleration2Test {
    @Test
    public void acceleration() {
        int xPos = 10;
        int xAMove = 0;
        int xVMove = 0;
        int xOutAcceleration = 0;
        int xVelocity = 0;

        //final int MaxMove = 64;
        final int MaxMove = 60;
        final int MaxVelocity = MaxMove + MaxMove;

        //final int xGravitation = -10;
        //final int xGravitation = 10;

        final int xGravitation = -15;
        //final int xGravitation = 15;

        //final int xGravitation = -19;
        //final int xGravitation = 19;

        for (int cnt = 0; cnt < 300; cnt++) {
            xOutAcceleration = xGravitation;

            xAMove += (xOutAcceleration);

            //xVMove += (xVelocity + xOutAcceleration);
            //xVMove += calcVMove(xOutAcceleration, xVelocity + xAMove, MaxVelocity);
            //!!!xVMove += calcVMove(xAMove, xVelocity, MaxVelocity);
            xVMove += (xVelocity + xAMove);
            //xVMove = calcVMove(xAMove, xVMove, xVelocity, MaxVelocity);

            final int moveDir = calcMoveDir(xVMove, MaxMove);
            // Will Move?
            if (moveDir != 0) {
                // Blocked?
                if (((xPos + moveDir) == 20) || ((xPos + moveDir) == 0)) {
                    System.out.printf("xPos:%2d, xAMove:%4d, xVMove:%4d, xVelocity:%4d\t <- HIT-BEVOR  ----\n", xPos, xAMove, xVMove, xVelocity);
                    //xVMove -= ((MaxMove + MaxMove) * moveDir);
                    xVelocity = -xVelocity;
                    //xVelocity = (calcNewVelocity(xVelocity, xAMove, MaxVelocity));
                    //xVMove += xVelocity - ((MaxMove + MaxMove) * moveDir); // Korrektur-Hack!!!! //xVMove += 30;  für moveDir = -1
                    xVMove = -xVMove;
                    xVMove -= (xVelocity + xAMove);
                    //xVMove -= (MaxMove + MaxMove) * moveDir;
                    //xAMove = 0;
                    //xAMove = -xAMove;
                    xAMove -= xOutAcceleration;
                    System.out.printf("xPos:%2d, xAMove:%4d, xVMove:%4d, xVelocity:%4d\t <- HIT-AFTER  ----\n", xPos, xAMove, xVMove, xVelocity);
                } else {
                    // Move:
                    System.out.printf("xPos:%2d, xAMove:%4d, xVMove:%4d, xVelocity:%4d\t <- MOVE-BEVOR\n", xPos, xAMove, xVMove, xVelocity);

                    xVelocity += xAMove;
                    //xVelocity = calcNewVelocity(xVelocity, xAMove, MaxVelocity);

                    //xVMove = calcVMove(xVMove, xVelocity, MaxVelocity);
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
        }
    }

    private static int calcVMove(int xVMove, int xVelocity, int MaxVelocity) {
        //xVMove += xVelocity + xAMove;
        //xVMove += xVelocity + xOutAcceleration;
        xVMove += xVelocity + calcNewAcceleration(xVelocity, xVMove, MaxVelocity);
        //xVMove += xVelocity + calcNewAcceleration(xVelocity, xOutAcceleration, MaxVelocity);
        return xVMove;
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

    private static int calcNewAcceleration(final int xVelocity, final int xVMove, final int MaxVelocity) {
        // m(v) = m0 / (sqrt(1 - (v² / c²)))
        // m(v) = m0 * (c / (c - v))
        //int nv = xVelocity + xAMove;
        //int nm = Math.ceilDiv((MaxVelocity - nv) * xVMove, MaxVelocity);

        //int nm = Math.ceilDiv(((MaxVelocity - Math.abs(xVelocity)) * xVMove), MaxVelocity);
        //int nm = (int)Math.floorDiv((long)((MaxVelocity - Math.abs(xVelocity)) * xVMove), (long)MaxVelocity);
        int nm = ((MaxVelocity - Math.abs(xVelocity)) * xVMove) / MaxVelocity;

        return nm;// + (xVMove == 0 ? 0 : (xVMove > 0 ? 1 : -1));
    }

    private static int calcNewVelocity(final int xVelocity, final int xVMove, final int MaxVelocity) {
        int nm = calcNewAcceleration(xVelocity, xVMove, MaxVelocity);

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

    @Test
    public void testDiv() {
        final int MaxMove = 10;
        final int MaxVelocity = MaxMove + MaxMove;

        for (int v = -MaxVelocity; v <= MaxVelocity; v += 1) {

            int nm = v / MaxMove;
            //int nm = Math.ceilDiv(v, MaxMove);
            //int nm = (int)Math.floorDiv((long)v, (long)MaxMove);

            System.out.printf("v:%4d, nm:%4d\n", v, nm);
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

    @Test
    public void testCalcMoveDir() {
        final int MaxMove = 60;
        final int MaxVelocity = MaxMove + MaxMove;
        {
            int xVMove = 0;
            MatcherAssert.assertThat(calcMoveDir(xVMove, MaxMove), Matchers.equalTo(0));
            xVMove += MaxMove;
            MatcherAssert.assertThat(calcMoveDir(xVMove, MaxMove), Matchers.equalTo(1));
        }
        {
            int xVMove = -(MaxMove - 1);
            MatcherAssert.assertThat(calcMoveDir(xVMove, MaxMove), Matchers.equalTo(0));
            xVMove += MaxVelocity;
            MatcherAssert.assertThat(calcMoveDir(xVMove, MaxMove), Matchers.equalTo(1));
        }
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

    private static int calcMoveDir2(final int xAMove, final int xVMove, final int MaxMove) {
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
