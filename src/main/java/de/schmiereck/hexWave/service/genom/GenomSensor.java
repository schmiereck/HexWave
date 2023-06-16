package de.schmiereck.hexWave.service.genom;

public class GenomSensor implements GenomInputInterface {
    public enum InputName {
        /**
         * Used for Bias.
         */
        Positive,
        Negative,
        Random,
        NeigbourPartTypeAP, NeigbourPartTypeAN,
        NeigbourPartTypeBP, NeigbourPartTypeBN,
        NeigbourPartTypeCP, NeigbourPartTypeCN
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
