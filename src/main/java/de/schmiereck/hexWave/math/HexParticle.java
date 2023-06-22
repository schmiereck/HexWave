package de.schmiereck.hexWave.math;

import java.io.Serializable;

public class HexParticle implements Serializable {
    public final HexVector velocityHexVector;
    public final MoveHexVector moveHexVector;
    public final int mass;

    public HexParticle(final int mass) {
        this.velocityHexVector = new HexVector();
        this.moveHexVector = new MoveHexVector();
        this.mass = mass;
    }

    public HexVector getVelocityHexVector() {
        return this.velocityHexVector;
    }

    public MoveHexVector getMoveHexVector() {
        return this.moveHexVector;
    }

    public int getMass() {
        return this.mass;
    }
}
