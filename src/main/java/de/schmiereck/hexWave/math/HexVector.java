package de.schmiereck.hexWave.math;

import java.io.Serializable;

public class HexVector implements Serializable {
    public int a, b, c;

    public void reset() {
        this.a = 0;
        this.b = 0;
        this.c = 0;
    }

    public void add(final HexVector hexVector) {
        this.a += hexVector.a;
        this.b += hexVector.b;
        this.c += hexVector.c;
    }
}
