package de.schmiereck.hexWave.service.brain;

import de.schmiereck.hexWave.service.genom.GenomOutput;

import java.io.Serializable;

public class BrainOutput implements Serializable {
    public final GenomOutput genomOutput;

    public final BrainInputInterface brainInput;

    public double outValue;

    public BrainOutput(final GenomOutput genomOutput, final BrainInputInterface brainInput) {
        this.genomOutput = genomOutput;
        this.brainInput = brainInput;
    }
}
