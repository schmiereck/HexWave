package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.HexParticle;
import de.schmiereck.hexWave2.math.ProbabilityVector;

import java.io.Serializable;

public class Part implements Serializable {
    private final Particle particle;

    private int count;

    public Cell.Dir rotationDir;

    public final ProbabilityVector probabilityVector;
    private int probability;

    public Part(final Particle particle,
                final int count, Cell.Dir rotationDir, final ProbabilityVector probabilityVector, final int probability) {
        this.particle = particle;
        this.count = count;
        this.rotationDir = rotationDir;
        this.probabilityVector = probabilityVector;
        this.probability = probability;
    }

    public Particle getParticle() {
        return this.particle;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public void setProbability(final int probability) {
        this.probability = probability;
    }

    public int getProbability() {
        return this.probability;
    }

}
