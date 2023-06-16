package de.schmiereck.hexWave.service.hexGrid;

public class PartField {
    private final Part part;
    private final PartField parentPartField;
    private final FieldType fieldType;
    private final int parentAreaDistance;

    public PartField(final Part part, final FieldType fieldType) {
        this.part = part;
        this.fieldType = fieldType;
        this.parentPartField = null;
        this.parentAreaDistance = 1;
    }

    public PartField(final PartField parentPartField, final FieldType fieldType, final int parentAreaDistance) {
        this.part = parentPartField.part;
        this.fieldType = fieldType;
        this.parentPartField = parentPartField;
        this.parentAreaDistance = parentAreaDistance;
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

}
