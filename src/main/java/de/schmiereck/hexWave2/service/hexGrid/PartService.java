package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.ProbabilityVector;

public final class PartService {

    private PartService() {
    }

    public static Particle.PartSubType calcOppositeSubType(final Particle particle) {
        return
            switch (particle.getPartType()) {
                case Field -> switch (particle.getPartSubType()) {
                    case FieldN -> Particle.PartSubType.FieldP;
                    case FieldP -> Particle.PartSubType.FieldN;
                    default -> null;
                };
                default -> null;
            };
    }

    public static int calcProbabilitySumAndResetDirProbability(final Part part) {
        final ProbabilityVector probabilityVector = part.probabilityVector;
        int probabilitySum = part.getProbability();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            probabilitySum += probabilityVector.getDirProbability(dir);
            probabilityVector.setDirProbability(dir, 0);
        }
        return probabilitySum;
    }

    public static int calcProbabilitySum(final Part part) {
        final ProbabilityVector probabilityVector = part.probabilityVector;
        int probabilitySum = part.getProbability();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            probabilitySum += probabilityVector.getDirProbability(dir);
        }
        return probabilitySum;
    }

}
