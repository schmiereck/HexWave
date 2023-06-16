package de.schmiereck.hexWave.service.hexGrid;

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

    private double energy;

    public Part(final PartType partType, final FieldType fieldType, final double energy, final boolean useField) {
        this.partType = partType;
        this.fieldType = fieldType;
        this.energy = energy;
        this.partField = useField ? new PartField(this, fieldType) : null;
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
}
