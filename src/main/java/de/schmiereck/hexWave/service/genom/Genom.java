package de.schmiereck.hexWave.service.genom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;


public class Genom implements Serializable {
    public final List<GenomSensor> genomSensorList = new ArrayList<>();

    public final Map<Integer, GenomInputInterface> genomInputMap = new HashMap<>();
    public final List<GenomNeuron> genomNeuronList = new ArrayList<>();
    public final List<GenomOutput> genomOutputList = new ArrayList<>();
    public int nextId = 0;
}
