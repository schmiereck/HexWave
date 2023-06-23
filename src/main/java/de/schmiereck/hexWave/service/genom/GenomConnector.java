package de.schmiereck.hexWave.service.genom;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(scope = GenomConnector.class, generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class GenomConnector implements Serializable {
    public final int genomInputId;
    public final double weight;
    public final double bias;

    public GenomConnector() {
        this.genomInputId = -1;
        this.weight = 0;
        this.bias = 0;
    }

    public GenomConnector(final int genomInputId, final double weight, final double bias) {
        this.genomInputId = genomInputId;
        this.weight = weight;
        this.bias = bias;
    }
}
