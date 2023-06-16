package de.schmiereck.hexWave.service.brain;

import de.schmiereck.hexWave.service.genom.GenomNeuron;

public class BrainNeuron implements BrainInputInterface {
    public final GenomNeuron genomNeuron;

    public BrainConnector[] brainConnectorArr;
    public double outValue;

    public BrainNeuron(final GenomNeuron genomNeuron) {
        this.genomNeuron = genomNeuron;
    }

    @Override
    public double getOutValue() {
        return this.outValue;
    }
}
