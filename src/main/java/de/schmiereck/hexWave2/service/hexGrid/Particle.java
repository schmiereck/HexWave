package de.schmiereck.hexWave2.service.hexGrid;

public class Particle {

    public enum PartType {
        Nothing,
        Particle,
        Field,
        Wall,
    }

    public enum PartSubType {
        Nothing,
        ParticleE,
        ParticleWall,
        FieldP,
        FieldN,
        FieldWall,
    }

    private final PartType partType;
    private final PartSubType partSubType;
    private final Particle fieldParticle;
    //private final Particle parentParticle;

    public Particle(final PartType partType, final PartSubType partSubType, final Particle fieldParticle) {
        this.partSubType = partSubType;
        this.partType = partType;
        this.fieldParticle = fieldParticle;
    }

    public PartType getPartType() {
        return this.partType;
    }

    public PartSubType getPartSubType() {
        return this.partSubType;
    }

    public Particle getFieldParticle() {
        return this.fieldParticle;
    }

}
