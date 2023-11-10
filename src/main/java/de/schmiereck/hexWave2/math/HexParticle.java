package de.schmiereck.hexWave2.math;

import java.io.Serializable;

public class HexParticle implements Serializable {
    public final HexVector inAccelerationHexVector;
    public final HexVector outAccelerationHexVector;
    public final HexVector velocityHexVector;
    public final MoveHexVector vMoveHexVector;
    public final MoveHexVector aMoveHexVector;
    public final int mass;

    public HexParticle(final int mass) {
        this.inAccelerationHexVector = new HexVector();
        this.outAccelerationHexVector = new HexVector();
        this.velocityHexVector = new HexVector();
        this.vMoveHexVector = new MoveHexVector();
        this.aMoveHexVector = new MoveHexVector();
        this.mass = mass;
    }

    public HexVector getInAccelerationHexVector() {
        return this.inAccelerationHexVector;
    }

    public HexVector getOutAccelerationHexVector() {
        return this.outAccelerationHexVector;
    }

    public HexVector getVelocityHexVector() {
        return this.velocityHexVector;
    }

    public MoveHexVector getVMoveHexVector() {
        return this.vMoveHexVector;
    }

    public MoveHexVector getAMoveHexVector() {
        return this.aMoveHexVector;
    }

    public int getMass() {
        return this.mass;
    }
}
