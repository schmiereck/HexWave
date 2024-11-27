package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.ProbabilityVector;

import java.util.Objects;

public class HashPart {
    final Particle.PartType partType;
    final Particle.PartSubType partSubType;
    final HashProbabilityVector impulseProbabilityVector;
    final int potentialProbability;

    public HashPart(final Particle.PartType partType, final Particle.PartSubType partSubType, final HashProbabilityVector impulseProbabilityVector, final int potentialProbability) {
        this.partType = partType;
        this.partSubType = partSubType;
        this.impulseProbabilityVector = impulseProbabilityVector;
        this.potentialProbability = potentialProbability;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HashPart hashPart = (HashPart) o;
        return this.potentialProbability == hashPart.potentialProbability &&
                this.partType == hashPart.partType &&
                this.partSubType == hashPart.partSubType &&
                Objects.equals(this.impulseProbabilityVector, hashPart.impulseProbabilityVector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.partType, this.partSubType, this.impulseProbabilityVector, this.potentialProbability);
    }
}
