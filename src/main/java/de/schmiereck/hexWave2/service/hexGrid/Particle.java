package de.schmiereck.hexWave2.service.hexGrid;

public class Particle {

    public enum PartType {
        Nothing,
        Particle,
        Field,
        Wall,
    }

    private final PartType partType;
    private final Particle fieldParticle;
    //private final Particle parentParticle;

    public Particle(final PartType partType, final Particle fieldParticle) {
        this.partType = partType;
        this.fieldParticle = fieldParticle;
    }

    public PartType getPartType() {
        return this.partType;
    }

    public Particle getFieldParticle() {
        return this.fieldParticle;
    }

}
