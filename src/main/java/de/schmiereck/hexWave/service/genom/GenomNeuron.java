package de.schmiereck.hexWave.service.genom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "neuronId")
public class GenomNeuron implements GenomInputInterface, Serializable {
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
