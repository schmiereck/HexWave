package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.ProbabilityVector;

public final class PartService {

    private PartService() {
    }

    public static Particle.PartSubType calcOppositeSubType(final Particle particle) {
        return calcOppositeSubType(particle.getPartType(), particle.getPartSubType());
    }

    public static Particle.PartSubType calcOppositeSubType(final Particle.PartType partType, final Particle.PartSubType partSubType) {
        return
            switch (partType) {
                case Field -> switch (partSubType) {
                    case FieldN -> Particle.PartSubType.FieldP;
                    case FieldP -> Particle.PartSubType.FieldN;
                    default -> null;
                };
                default -> null;
            };
    }

    public static int calcProbabilitySumAndResetDirPotentialProbability(final Part part) {
        int probabilitySum = part.getPotentialProbability();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            probabilitySum += part.getPotentialProbabilityByDir(dir);
            part.setPotentialProbabilityForDir(dir, 0);
        }
        return probabilitySum;
    }

    public static void calcResetDirPotentialProbability(final Part part) {
        for (final Cell.Dir dir : Cell.Dir.values()) {
            part.setPotentialProbabilityForDir(dir, 0);
        }
    }

    public static int calcPotentialProbabilitySum(final Part part) {
        int probabilitySum = part.getPotentialProbability();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            probabilitySum += part.getPotentialProbabilityByDir(dir);
        }
        return probabilitySum;
    }

}
