package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.HexParticle;
import de.schmiereck.hexWave2.math.ProbabilityVector;

import java.io.Serializable;

public class Part implements Serializable {
    private final Particle particle;

    public Cell.Dir rotationDir;

    public final ProbabilityVector probabilityVector;
    private int probability;
    private int[] probabilityDirArr = new int[Cell.Dir.values().length];

    public Part(final Particle particle,
                final Cell.Dir rotationDir, final ProbabilityVector probabilityVector, final int probability) {
        this.particle = particle;
        this.rotationDir = rotationDir;
        this.probabilityVector = probabilityVector;
        this.probability = probability;
    }

    public Particle getParticle() {
        return this.particle;
    }

    public void setProbability(final int probability) {
        if (probability < 0) {
            throw new RuntimeException("probability < 0.");
        }
        this.probability = probability;
    }

    public int getProbability() {
        return this.probability;
    }

    public void setDirProbability(final Cell.Dir dir, final int probability) {
        if (probability < 0) {
            throw new RuntimeException("dirProbability < 0.");
        }
        this.probabilityDirArr[dir.ordinal()] = probability;
    }

    public int getDirProbability(final Cell.Dir dir) {
        return this.probabilityDirArr[dir.ordinal()];
    }

}
