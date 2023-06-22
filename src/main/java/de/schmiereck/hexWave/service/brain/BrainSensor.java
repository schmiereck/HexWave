package de.schmiereck.hexWave.service.brain;

import de.schmiereck.hexWave.service.genom.GenomSensor;

import java.io.Serializable;

public class BrainSensor implements BrainInputInterface, Serializable {
    public final GenomSensor genomSensor;
    public double inValue;

    public BrainSensor(final GenomSensor genomSensor) {
        this.genomSensor = genomSensor;
    }

    @Override
    public double getOutValue() {
        return this.inValue;
    }
}
