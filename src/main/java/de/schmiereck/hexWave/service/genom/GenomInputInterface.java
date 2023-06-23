package de.schmiereck.hexWave.service.genom;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * https://www.baeldung.com/jackson-inheritance
 */
//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)//, property = "className")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS )//, property = "className")
//@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION )//, property = "className")
/*
@JsonSubTypes({
        @JsonSubTypes.Type(value = GenomSensor.class, name = "GenomSensor"),
        @JsonSubTypes.Type(value = GenomNeuron.class, name = "GenomNeuron")
})
*/
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonIdentityInfo(scope = GenomInputInterface.class, generator = ObjectIdGenerators.IntSequenceGenerator.class)//, property = "inputId")
public interface GenomInputInterface {

    int getInputId();

}
