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

    public static int calcProbabilitySumAndResetDirProbability(final Part part) {
        final ProbabilityVector probabilityVector = part.impulseProbabilityVector;
        int probabilitySum = part.getPotentialProbability();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            probabilitySum += part.getDirProbability(dir);
            part.setDirProbability(dir, 0);
        }
        return probabilitySum;
    }

    public static void calcResetDirProbability(final Part part) {
        for (final Cell.Dir dir : Cell.Dir.values()) {
            part.setDirProbability(dir, 0);
        }
    }

    public static int calcProbabilitySum(final Part part) {
        int probabilitySum = part.getPotentialProbability();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            probabilitySum += part.getDirProbability(dir);
        }
        return probabilitySum;
    }

}
