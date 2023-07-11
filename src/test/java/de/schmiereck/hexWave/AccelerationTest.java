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
        //final int xGravitation = -10;
        final int xGravitation = 10;
        //final int xGravitation = -19;

        for (int cnt = 0; cnt < 300; cnt++) {
            xOutAcceleration = xGravitation;

            xAMove += xOutAcceleration;

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
            // Will Move?
            if (moveDir != 0) {
                // Blocked?
                if (((xPos + moveDir) == 20) || ((xPos + moveDir) == 0)) {
                    System.out.printf("xPos:%2d, xVMove:%4d, xAMove:%4d, xVelocity:%4d\t <- HIT-BEVOR  ----\n", xPos, xVMove, xAMove, xVelocity);
                    xVMove -= (MaxMove + MaxMove) * moveDir;
                    xVelocity = -xVelocity;
                    xVMove += xVelocity - ((MaxMove + MaxMove) * -moveDir); // Korrektur-Hack!!!! //xVMove += 30;  für moveDir = -1
                    System.out.printf("xPos:%2d, xVMove:%4d, xAMove:%4d, xVelocity:%4d\t <- HIT-AFTER  ----\n", xPos, xVMove, xAMove, xVelocity);
                } else {
                    // Move:
                    System.out.printf("xPos:%2d, xVMove:%4d, xAMove:%4d, xVelocity:%4d\t <- MOVE-BEVOR\n", xPos, xVMove, xAMove, xVelocity);
                    xVelocity += xAMove;
                    xVMove -= (MaxMove + MaxMove) * moveDir;
                    xAMove = 0;
                    xPos += moveDir;
                    System.out.printf("xPos:%2d, xVMove:%4d, xAMove:%4d, xVelocity:%4d\t <- MOVE-AFTER\n", xPos, xVMove, xAMove, xVelocity);
                }
            } else {
                System.out.printf("xPos:%2d, xVMove:%4d, xAMove:%4d, xVelocity:%4d\t <- STAY-BEVORE\n", xPos, xVMove, xAMove, xVelocity);
                System.out.printf("xPos:%2d, xVMove:%4d, xAMove:%4d, xVelocity:%4d\t <- STAY-AFTER\n", xPos, xVMove, xAMove, xVelocity);
            }

            //if (Math.abs(xVelocity + xAMove) > MaxMove) {
            //    xVMove = MaxMove;
            //} else {
                xVMove += xVelocity + xAMove;
            //}
        }
    }
}
