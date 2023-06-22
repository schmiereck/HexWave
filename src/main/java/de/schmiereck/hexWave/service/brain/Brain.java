package de.schmiereck.hexWave.service.brain;

import de.schmiereck.hexWave.service.genom.Genom;
import de.schmiereck.hexWave.service.genom.GenomOutput;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.Part;

import java.io.Serializable;
import java.util.Objects;

public class Brain implements Serializable {
    private final Genom genom;
    public BrainSensor[] brainSensorArr;
    public BrainNeuron[] brainNeuronArr;
    public BrainOutput[] brainOutputArr;
    public BrainOutput[] brainOutputMapArr = new BrainOutput[GenomOutput.OutputName.values().length];

    public Brain(final Genom genom) {
        this.genom = genom;
    }

    public double getOutput(final GenomOutput.OutputName outputName) {
        final double outValue;
        final BrainOutput brainOutput = this.brainOutputMapArr[outputName.ordinal()];
        if (Objects.nonNull(brainOutput)) {
            outValue = brainOutput.outValue;
        } else {
            outValue = 0.0D;
        }
        return outValue;
    }

    public Genom getGenom() {
        return this.genom;
    }

    private static GenomOutput.OutputName EatNeighbourDirArr[] = new GenomOutput.OutputName[Cell.Dir.values().length];
    static {
        EatNeighbourDirArr[Cell.Dir.AP.ordinal()] = GenomOutput.OutputName.EatNeighbourAP;
        EatNeighbourDirArr[Cell.Dir.AN.ordinal()] = GenomOutput.OutputName.EatNeighbourAN;
        EatNeighbourDirArr[Cell.Dir.BP.ordinal()] = GenomOutput.OutputName.EatNeighbourBP;
        EatNeighbourDirArr[Cell.Dir.BN.ordinal()] = GenomOutput.OutputName.EatNeighbourBN;
        EatNeighbourDirArr[Cell.Dir.CP.ordinal()] = GenomOutput.OutputName.EatNeighbourCP;
        EatNeighbourDirArr[Cell.Dir.CN.ordinal()] = GenomOutput.OutputName.EatNeighbourCN;
    }

    public double getEatNeighbourOutput(final Cell.Dir dir) {
        return this.getOutput(EatNeighbourDirArr[dir.ordinal()]);
    }
}
