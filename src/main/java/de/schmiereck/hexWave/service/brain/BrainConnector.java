package de.schmiereck.hexWave.service.brain;

import de.schmiereck.hexWave.service.genom.GenomConnector;

public class BrainConnector {
    public final GenomConnector genomConnector;
    public final BrainInputInterface brainInput;
    public double inValue;

    public BrainConnector(final GenomConnector genomConnector, final BrainInputInterface brainInput) {
        this.genomConnector = genomConnector;
        this.brainInput = brainInput;
    }
}
