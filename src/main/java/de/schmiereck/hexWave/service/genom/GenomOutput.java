package de.schmiereck.hexWave.service.genom;

public class GenomOutput {
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
