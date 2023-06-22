package de.schmiereck.hexWave.service.genom;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "outputId")
public class GenomOutput implements Serializable {
    public enum OutputName {
        MoveA,
        MoveB,
        MoveC,
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

    public GenomOutput(final int outputId, final int genomInputId, final OutputName outputName) {
        this.outputId = outputId;
        this.genomInputId = genomInputId;
        this.outputName = outputName;
    }
}
