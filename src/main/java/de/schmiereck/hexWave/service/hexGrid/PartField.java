package de.schmiereck.hexWave.service.hexGrid;

public class PartField {
    private final Part part;
    private final PartField parentPartField;
    private final FieldType fieldType;
    private final int parentAreaDistance;
    private final double value;

    public PartField(final Part part, final FieldType fieldType, final double value) {
        this.part = part;
        this.fieldType = fieldType;
        this.parentPartField = null;
        this.parentAreaDistance = 0;
        this.value = value;
    }

    public PartField(final PartField parentPartField, final FieldType fieldType, final int parentAreaDistance, final double value) {
        this.part = parentPartField.part;
        this.fieldType = fieldType;
        this.parentPartField = parentPartField;
        this.parentAreaDistance = parentAreaDistance;
        this.value = value;
    }

    public Part getPart() {
        return this.part;
    }

    public PartField getParentPartField() {
        return this.parentPartField;
    }

    public int getParentAreaDistance() {
        return this.parentAreaDistance;
    }

    public FieldType getFieldType() {
        return this.fieldType;
    }

    public double getValue() {
        return this.value;
    }
}
