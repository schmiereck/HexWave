package de.schmiereck.hexWave2.service.hexGrid;

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
}
