package de.schmiereck.hexWave.service.hexGrid;

import de.schmiereck.hexWave.math.HexParticle;

public class Part {
    public enum PartType {
        Nothing,
        Air,
        Water,
        Wallpaper,
        Wall,
        Life,
        Sun
    }

    private final PartType partType;
    private final FieldType fieldType;
    private final PartField partField;
    private final HexParticle hexParticle;

    private double energy;

    public Part(final PartType partType, final FieldType fieldType, final double energy, final boolean useField, final int mass) {
        this.partType = partType;
        this.fieldType = fieldType;
        this.energy = energy;
        this.partField = useField ? new PartField(this, fieldType) : null;
        this.hexParticle = new HexParticle(mass);
    }

    public PartType getPartType() {
        return this.partType;
    }

    public PartField getPartField() {
        return this.partField;
    }

    public FieldType getFieldType() {
        return this.fieldType;
    }

    public double getEnergy() {
        return this.energy;
    }

    public void setEnergy(final double energy) {
        this.energy = energy;
    }

    public void addEnergy(final double energy) {
        this.energy += energy;
    }

    public HexParticle getHexParticle() {
        return this.hexParticle;
    }
}
