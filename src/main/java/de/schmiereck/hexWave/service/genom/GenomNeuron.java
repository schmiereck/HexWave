package de.schmiereck.hexWave.service.genom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

//@JsonIdentityInfo(scope = GenomNeuron.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "neuronId")
@JsonTypeName("GenomNeuron")
public class GenomNeuron implements GenomInputInterface, Serializable {
    //public final int neuronId;
    public int neuronId;

    public List<GenomConnector> genomConnectorList = new ArrayList<>();

    public GenomNeuron() {
        this.neuronId = -1;
    }

    @JsonCreator
    public GenomNeuron(@JsonProperty("neuronId")  final int neuronId) {
        this.neuronId = neuronId;
    }

    @JsonIgnore
    @Override
    public int getInputId() {
        return this.neuronId;
    }

}
