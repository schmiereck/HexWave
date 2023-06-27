package de.schmiereck.hexWave.service.genom;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

//@JsonIdentityInfo(scope = GenomSensor.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "sensorId")
@JsonTypeName("GenomSensor")
public class GenomSensor implements GenomInputInterface, Serializable {
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

        NeigbourEnergyAP, NeigbourEnergyAN,
        NeigbourEnergyBP, NeigbourEnergyBN,
        NeigbourEnergyCP, NeigbourEnergyCN,

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

    //public final int sensorId;
    public int sensorId;

    public final InputName inputName;


    public GenomSensor() {
        this.sensorId = -1;
        this.inputName = null;
    }


    @JsonCreator
    public GenomSensor(@JsonProperty("sensorId") final int sensorId, @JsonProperty("inputName") final InputName inputName) {
        this.sensorId = sensorId;
        this.inputName = inputName;
    }

    @JsonIgnore
    @Override
    public int getInputId() {
        return this.sensorId;
    }

}
