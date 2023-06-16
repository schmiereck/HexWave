package de.schmiereck.hexWave.service.genom;

import java.util.ArrayList;
import java.util.List;

public class GenomNeuron implements GenomInputInterface {
    public final int neuronId;

    public List<GenomConnector> genomConnectorList = new ArrayList<>();

    public GenomNeuron(final int neuronId) {
        this.neuronId = neuronId;
    }

    @Override
    public int getInputId() {
        return this.neuronId;
    }
}
