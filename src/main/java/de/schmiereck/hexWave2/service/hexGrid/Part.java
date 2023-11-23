package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.ProbabilityVector;

import java.io.Serializable;

public class Part implements Serializable {
    private final Particle particle;

    public Cell.Dir rotationDir;

    public final ProbabilityVector impulseProbabilityVector;
    private int potentialProbability;
    private int[] potentialProbabilityDirArr = new int[Cell.Dir.values().length];

    public Part(final Particle particle,
                final Cell.Dir rotationDir, final ProbabilityVector impulseProbabilityVector, final int potentialProbability) {
        this.particle = particle;
        this.rotationDir = rotationDir;
        this.impulseProbabilityVector = impulseProbabilityVector;
        this.potentialProbability = potentialProbability;
    }

    public Particle getParticle() {
        return this.particle;
    }

    public void setPotentialProbability(final int potentialProbability) {
        if (potentialProbability < 0) {
            throw new RuntimeException("probability < 0.");
        }
        this.potentialProbability = potentialProbability;
    }

    public int getPotentialProbability() {
        return this.potentialProbability;
    }

    public void setDirProbability(final Cell.Dir dir, final int probability) {
        if (probability < 0) {
            throw new RuntimeException("dirProbability < 0.");
        }
        this.potentialProbabilityDirArr[dir.ordinal()] = probability;
    }

    public int getDirProbability(final Cell.Dir dir) {
        return this.potentialProbabilityDirArr[dir.ordinal()];
    }

}
