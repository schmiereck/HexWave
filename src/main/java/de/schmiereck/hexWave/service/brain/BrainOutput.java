package de.schmiereck.hexWave.service.brain;

import de.schmiereck.hexWave.service.genom.GenomOutput;

public class BrainOutput {
    public final GenomOutput genomOutput;

    public final BrainInputInterface brainInput;

    public double outValue;

    public BrainOutput(final GenomOutput genomOutput, final BrainInputInterface brainInput) {
        this.genomOutput = genomOutput;
        this.brainInput = brainInput;
    }
}
