import org.junit.jupiter.api.Test;

/**
 * Die folgende Software simuliert ein gravitativ beschleunigtes Teilchen welches vom Boden abprallt.
 * Allerdings ist die Geschwindigkeit zwischen den unelastischen Kollisionen verschieden.
 * Finde den Fehler.
 */
public class Acceleration5Test {
    @Test
    public void acceleration() {
        int xPos = 2;
        int xVMove = 0;
        int xOutAcceleration = 0;
        int xVelocity = 0;

        final int MaxMove = 60;
        //final int MaxVelocity = MaxMove + MaxMove;
        final int MaxVelocity = MaxMove * 1;

        //final int xGravitation = -1;

        //final int xGravitation = -5;
        //final int xGravitation = 5;

        //final int xGravitation = -10;
        //final int xGravitation = 10;

        //final int xGravitation = -15;
        //final int xGravitation = 15;

        final int xGravitation = -19;
        //final int xGravitation = 19;

        for (int cnt = 0; cnt < 300; cnt++) {
            xOutAcceleration = xGravitation;

            final int newAcceleration = calcNewAccelerationMiddle(xVelocity, xOutAcceleration, MaxVelocity);
            int newXVelocity = xVelocity + newAcceleration;
            //int newXVelocity = xVelocity + calcNewAccelerationMinMax(xVelocity, xOutAcceleration, MaxVelocity);
            //int newXVelocity = xVelocity + calcNewAcceleration2(xVelocity, xOutAcceleration, MaxVelocity);
            //int newXVelocity = xVelocity + calcNewAcceleration(xVelocity, xOutAcceleration, MaxVelocity);

            //xVMove = calcVMove(xVMove, newXVelocity);
            int newXVMove = calcVMove(xVMove, newXVelocity);
            int new2XVMove = calcVMove(xVMove, xVelocity);

            // energie berechnen und ausgeben: Höhe + Velocity + Move
            //int e = (xPos * MaxMove) + Math.abs(xVelocity) + Math.abs(xVMove);
            int e = (xPos * MaxMove) + Math.abs(xVelocity);

            //final int moveDir = calcMoveDir(xVMove, MaxMove);
            final int moveDir = calcMoveDir(newXVMove, MaxMove);
            // Will Move?
            if (moveDir != 0) {
                // Blocked?
                if (checkBlocked(xPos, moveDir)) {
                    System.out.printf("xPos:%2d, xVMove:%4d, newXVMove:%4d, newAcceleration:%4d, xVelocity:%4d, e:%4d\t <- HIT-BEVOR  ----\n", xPos, xVMove, newXVMove, newAcceleration, xVelocity, e);
                    xVelocity = -xVelocity;
                    //xVelocity = -newXVelocity;
                    //xVMove = xVMove - ((MaxMove + MaxMove) * moveDir);
                    //xVMove = newXVMove - ((MaxMove) * moveDir);
                    xVMove = newXVMove - ((MaxMove + MaxMove) * moveDir);
//                    xVMove = -xVMove;
//                    xVMove = 0;
                    System.out.printf("xPos:%2d, xVMove:%4d, newXVMove:%4d, newAcceleration:%4d, xVelocity:%4d, e:%4d\t <- HIT-AFTER  ----\n", xPos, xVMove, newXVMove, newAcceleration, xVelocity, e);
                } else {
                    // Move:
                    System.out.printf("xPos:%2d, xVMove:%4d, newXVMove:%4d, newAcceleration:%4d, xVelocity:%4d, e:%4d\t <- MOVE-BEVOR\n", xPos, xVMove, newXVMove, newAcceleration, xVelocity, e);
//                    xVMove = calcVMove(xVMove, newXVelocity);
                    xVelocity = newXVelocity;

                    //xVMove = new2XVMove - ((MaxMove + MaxMove) * moveDir);
                    xVMove = newXVMove - ((MaxMove + MaxMove) * moveDir);
                    //xVMove = newXVMove - ((MaxMove) * moveDir);

                    xPos += moveDir;
                    System.out.printf("xPos:%2d, xVMove:%4d, newXVMove:%4d, newAcceleration:%4d, xVelocity:%4d, e:%4d\t <- MOVE-AFTER\n", xPos, xVMove, newXVMove, newAcceleration, xVelocity, e);
                }
            } else {
                System.out.printf("xPos:%2d, xVMove:%4d, newXVMove:%4d, newAcceleration:%4d, xVelocity:%4d, e:%4d\t <- STAY-BEVORE\n", xPos, xVMove, newXVMove, newAcceleration, xVelocity, e);
//                xVMove = calcVMove(xVMove, newXVelocity);
                //xVMove = newXVMove;
                xVMove = newXVMove;
                xVelocity = newXVelocity;
                System.out.printf("xPos:%2d, xVMove:%4d, newXVMove:%4d, newAcceleration:%4d, xVelocity:%4d, e:%4d\t <- STAY-AFTER\n", xPos, xVMove, newXVMove, newAcceleration, xVelocity, e);
            }
        }
    }

    private static int calcMinMax(final int v, final int max) {
        return Math.min(Math.max(v, -max), max);
    }

    @Test
    public void testcalcNewAcceleration() {
        final int MaxVelocity = 100;
        for (int pos = -20; pos <= 20; pos++)
        {
            final int xVelocity = 10;
            final int xAcceleration = 10 * pos;
            final int a = calcNewAcceleration(xVelocity, xAcceleration, MaxVelocity);
            System.out.printf("newVelocity:%4d = a:%4d <- %4d, xVelocity:%4d, MaxVelocity:%4d\n", xVelocity + a, a, xAcceleration, xVelocity, MaxVelocity);
        }
    }

    private static int calcNewAcceleration(final int xVelocity, final int xAcceleration, final int MaxVelocity) {
        // m(v) = m0 / (sqrt(1 - (v² / c²)))
        // m(v) = m0 * (c / (c - v))

        final int saveVelocity = calcMinMax(xVelocity, MaxVelocity);

        final int mass0 = 1;

        int na;
        //na = (int)Math.floorDiv((long)((MaxVelocity - Math.abs(xVelocity)) * newAcceleration), (long)MaxVelocity);
        //na = ((MaxVelocity - Math.abs(xVelocity)) * newAcceleration) / MaxVelocity;

        //na = ((MaxVelocity - Math.abs(xVelocity)) * xAcceleration) / MaxVelocity;

        //na = xAcceleration;

        //int newXVelocity = calcMinMax(xVelocity + xAcceleration, MaxVelocity);

        //na = newXVelocity - xVelocity;

        //na = Math.ceilDiv(((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration), MaxVelocity);
        //na = (int)Math.floorDiv((long)((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration), (long)MaxVelocity);
        //na = divideAndRoundUp(((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration), MaxVelocity);
        //na = ((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration) / MaxVelocity;

        //final int uv = (MaxVelocity - Math.abs(saveVelocity));
        //final int mass1 = (mass0 * uv) / MaxVelocity;
        //final int force = mass1 * xAcceleration;

        na = (xAcceleration * (MaxVelocity - Math.abs(saveVelocity))) / MaxVelocity;
        na = calcMinMax(xVelocity + na, MaxVelocity) - xVelocity;

        return na;
    }

    private static int calcNewAccelerationMiddle(final int xVelocity, final int xAcceleration, final int MaxVelocity) {
        int newXVelocity = xVelocity + xAcceleration;

        final int saveVelocity = calcMinMax(newXVelocity, MaxVelocity);
        //final int middleVelocity = (xVelocity + newXVelocity) / 2;
        //final int middleVelocity = (xVelocity + newXVelocity);
        final int middleVelocity = Math.floorDiv((xVelocity + newXVelocity), 2);

        final int mass0 = 1;

        int na;
        //na = (int)Math.floorDiv((long)((MaxVelocity - Math.abs(xVelocity)) * newAcceleration), (long)MaxVelocity);
        //na = ((MaxVelocity - Math.abs(xVelocity)) * newAcceleration) / MaxVelocity;

        //na = ((MaxVelocity - Math.abs(xVelocity)) * xAcceleration) / MaxVelocity;

        //na = xAcceleration;

        //int newXVelocity = calcMinMax(xVelocity + xAcceleration, MaxVelocity);

        //na = newXVelocity - xVelocity;

        //na = Math.ceilDiv(((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration), MaxVelocity);
        //na = (int)Math.floorDiv((long)((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration), (long)MaxVelocity);
        //na = divideAndRoundUp(((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration), MaxVelocity);
        //na = ((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration) / MaxVelocity;

        //final int uv = (MaxVelocity - Math.abs(saveVelocity));
        //final int mass1 = (mass0 * uv) / MaxVelocity;
        //final int force = mass1 * xAcceleration;

        //na = (xAcceleration * ((MaxVelocity * 2) - Math.abs(middleVelocity))) / (MaxVelocity * 2);
        //na = divideAndRoundUp((xAcceleration * ((MaxVelocity * 2) - Math.abs(middleVelocity))), (MaxVelocity * 2));
        na = Math.floorDiv((xAcceleration * ((MaxVelocity * 2) - Math.abs(middleVelocity))), (MaxVelocity * 2));
        //na = calcMinMax(xVelocity + na, MaxVelocity) - xVelocity;

        return na;
    }

    @Test
    public void testDiv() {
        testDivideAndRoundUp(0, 1);

        testDivideAndRoundUp(1, 1);
        testDivideAndRoundUp(1, 2);
        testDivideAndRoundUp(1, 3);
        testDivideAndRoundUp(1, 4);
        testDivideAndRoundUp(1, 5);
        testDivideAndRoundUp(1, 5000);

        testDivideAndRoundUp(2, 1);
        testDivideAndRoundUp(2, 2);
        testDivideAndRoundUp(2, 3);
        testDivideAndRoundUp(2, 30000);

        testDivideAndRoundUp(100, 10);
        testDivideAndRoundUp(100, 40);
        testDivideAndRoundUp(100, 50);
        testDivideAndRoundUp(100, 60);
        testDivideAndRoundUp(100, 10);
        testDivideAndRoundUp(100, 90);
        testDivideAndRoundUp(100, 99);
        testDivideAndRoundUp(100, 100);
        testDivideAndRoundUp(100, 101);

        testDivideAndRoundUp(119, 120);
        testDivideAndRoundUp(120, 120);
        testDivideAndRoundUp(120, 119);
    }

    private void testDivideAndRoundUp(int a, int b) {
        //System.out.printf("%3d = %3d / %3d (mod:%3d)\n", divideAndRoundUp(a, b), a, b, (a % b));
        System.out.printf("%3d, %3d, %3d = %3d / %3d (mod:%3d)\n", divideAndRoundUp(a, b), Math.ceilDiv(a, b), Math.floorDiv(a, b), a, b, (a % b));
    }

    private static int calcNewAccelerationSimple(final int xVelocity, final int xAcceleration, final int MaxVelocity) {
        int na;
        //na = (int)Math.floorDiv((long)((MaxVelocity - Math.abs(xVelocity)) * newAcceleration), (long)MaxVelocity);
        //na = ((MaxVelocity - Math.abs(xVelocity)) * newAcceleration) / MaxVelocity;

        //na = ((MaxVelocity - Math.abs(xVelocity)) * xAcceleration) / MaxVelocity;

        na = xAcceleration;

        //int newXVelocity = calcMinMax(xVelocity + xAcceleration, MaxVelocity);
        //na = newXVelocity - xVelocity;

        //na = Math.ceilDiv(((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration), MaxVelocity);
        //na = (int)Math.floorDiv((long)((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration), (long)MaxVelocity);
        //na = divideAndRoundUp(((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration), MaxVelocity);
        //na = ((MaxVelocity - Math.abs(newXVelocity)) * xAcceleration) / MaxVelocity;

        return na;
    }

    private static int calcNewAccelerationMinMax(final int xVelocity, final int xAcceleration, final int MaxVelocity) {
        int na;

        int newXVelocity = calcMinMax(xVelocity + xAcceleration, MaxVelocity);
        na = newXVelocity - xVelocity;

        return na;
    }

    public static int divideAndRoundUp(int num, int divisor) {
        int mod = num % divisor;

        int cor = (mod > 0 ? 1 : (mod < 0 ? -1 : 0));
        int div = (num / divisor);

        return div + cor;
    }

    public static int divideAndRoundUp2(int num, int divisor) {
        if (num == 0 || divisor == 0) { return 0; }

        int sign = (num > 0 ? 1 : -1) * (divisor > 0 ? 1 : -1);

        if (sign > 0) {
            return (num + divisor - 1) / divisor;
        }
        else {
            return (num / divisor);
        }
    }

    private static boolean checkBlocked(int xPos, int moveDir) {
        return ((xPos + moveDir) == 20) || ((xPos + moveDir) == 0);
    }

    private static int calcVMove(int xVMove, int xVelocity) {
        return xVMove + xVelocity;
    }

    private static int calcMoveDir(final int xVMove, final int MaxMove) {
        final int moveDir;
        if (xVMove > MaxMove) {
            moveDir = 1;
        } else {
            if (xVMove < -MaxMove) {
                moveDir = -1;
            } else {
                moveDir = 0;
            }
        }
        return moveDir;
    }
}
