package de.schmiereck.hexWave2.service.life;

import java.io.Serializable;

public class PartIdentity implements Serializable {
    public final double[] partIdentity = new double[3];

    public PartIdentity(final double partIdentity1, final double partIdentity2, final double partIdentity3) {
        this.partIdentity[0] = partIdentity1;
        this.partIdentity[1] = partIdentity2;
        this.partIdentity[2] = partIdentity3;
    }
}
