package de.schmiereck.hexWave.service.genom;

public class GenomConnector {
    public final int genomInputId;
    public final double weight;
    public final double bias;

    public GenomConnector(final int genomInputId, final double weight, final double bias) {
        this.genomInputId = genomInputId;
        this.weight = weight;
        this.bias = bias;
    }
}
