package de.schmiereck.hexWave.service.genom;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

//@JsonIdentityInfo(scope = GenomOutput.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "outputId")
@JsonIdentityInfo(scope = GenomOutput.class, generator = ObjectIdGenerators.IntSequenceGenerator.class)
@JsonTypeName("GenomOutput")
public class GenomOutput implements Serializable {
    public enum OutputName {
        EatNeighbourAP,
        EatNeighbourAN,
        EatNeighbourBP,
        EatNeighbourBN,
        EatNeighbourCP,
        EatNeighbourCN,
        PushFieldA,
        PushFieldB,
        PushFieldC,
        PullFieldA,
        PullFieldB,
        PullFieldC,
        ComFieldA,
        ComFieldB,
        ComFieldC,
        BirthA,
        BirthB,
        BirthC,
    }

    public final int outputId;
    public final int genomInputId;

    public final OutputName outputName;

    public GenomOutput() {
        this.outputId = -1;
        this.genomInputId = -1;
        this.outputName = null;
    }

    public GenomOutput(final int outputId, final int genomInputId, final OutputName outputName) {
        this.outputId = outputId;
        this.genomInputId = genomInputId;
        this.outputName = outputName;
    }
}
