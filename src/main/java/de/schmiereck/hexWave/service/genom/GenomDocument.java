package de.schmiereck.hexWave.service.genom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenomDocument {
    public List<Genom> genomList;

}
