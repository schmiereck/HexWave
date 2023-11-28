package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.ProbabilityVector;

import java.io.Serializable;

public class Part implements Serializable {
    private final Particle particle;

    public Cell.Dir rotationDir;
    public Cell.Dir lastExtraDir;

    public final ProbabilityVector impulseProbabilityVector;
    private int potentialProbability;
    private int[] dirPotentialProbabilityArr = new int[Cell.Dir.values().length];

    public Part(final Particle particle,
                final Cell.Dir rotationDir, final ProbabilityVector impulseProbabilityVector, final int potentialProbability) {
        this.particle = particle;
        this.rotationDir = rotationDir;
        this.lastExtraDir = null;
        this.impulseProbabilityVector = impulseProbabilityVector;
        this.potentialProbability = potentialProbability;
    }

    public Part(final Particle particle,
                final Cell.Dir rotationDir, final Cell.Dir lastExtraDir, final ProbabilityVector impulseProbabilityVector, final int potentialProbability) {
        this.particle = particle;
        this.rotationDir = rotationDir;
        this.lastExtraDir = lastExtraDir;
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

    public void setPotentialProbabilityForDir(final Cell.Dir dir, final int probability) {
        if (probability < 0) {
            throw new RuntimeException("dirProbability < 0.");
        }
        this.dirPotentialProbabilityArr[dir.ordinal()] = probability;
    }

    public int getPotentialProbabilityByDir(final Cell.Dir dir) {
        return this.dirPotentialProbabilityArr[dir.ordinal()];
    }

}
