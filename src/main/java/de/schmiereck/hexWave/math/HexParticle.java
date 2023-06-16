package de.schmiereck.hexWave.math;

public class HexParticle {
    public final HexVector velocityHexVector;
    public final MoveHexVector moveHexVector;
    public final int mass;

    public HexParticle() {
        this.velocityHexVector = new HexVector();
        this.moveHexVector = new MoveHexVector();
        this.mass = 8;
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
