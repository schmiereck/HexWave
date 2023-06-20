package de.schmiereck.hexWave.service.genom;

public class GenomSensor implements GenomInputInterface {
    public enum InputName {
        /**
         * Used for Bias.
         */
        Positive,
        Negative,
        Random,
        Energy,
        NeigbourPartTypeAP, NeigbourPartTypeAN,
        NeigbourPartTypeBP, NeigbourPartTypeBN,
        NeigbourPartTypeCP, NeigbourPartTypeCN,

        Part1FieldAP, Part1FieldAN,
        Part1FieldBP, Part1FieldBN,
        Part1FieldCP, Part1FieldCN,

        Part2FieldAP, Part2FieldAN,
        Part2FieldBP, Part2FieldBN,
        Part2FieldCP, Part2FieldCN,

        Part3FieldAP, Part3FieldAN,
        Part3FieldBP, Part3FieldBN,
        Part3FieldCP, Part3FieldCN,

        ComFieldAP, ComFieldAN,
        ComFieldBP, ComFieldBN,
        ComFieldCP,ComFieldCN,
    }

    public final int sensorId;

    public final InputName inputName;

    public GenomSensor(final int sensorId, final InputName inputName) {
        this.sensorId = sensorId;
        this.inputName = inputName;
    }

    @Override
    public int getInputId() {
        return this.sensorId;
    }

}
