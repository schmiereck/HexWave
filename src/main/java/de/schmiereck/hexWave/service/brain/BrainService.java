package de.schmiereck.hexWave.service.brain;

import de.schmiereck.hexWave.service.genom.Genom;
import de.schmiereck.hexWave.service.genom.GenomInputInterface;
import de.schmiereck.hexWave.service.genom.GenomNeuron;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.utils.MathUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class BrainService {
    private final Random rnd = new Random();

    public Brain createBrain(final Genom genom) {
        final Brain brain = new Brain(genom);

        final HashMap<Integer, BrainInputInterface> inputMap = new HashMap<>();

        // Sensor.
        brain.brainSensorArr = genom.genomSensorList.stream().
                map(genomSensor -> {
                    final BrainSensor brainSensor = new BrainSensor(genomSensor);
                    inputMap.put(genomSensor.getInputId(), brainSensor);
                    return brainSensor;
                }).toArray(BrainSensor[]::new);

        // Neuron.
        brain.brainNeuronArr = genom.genomNeuronList.stream().
                map(genomNeuron -> {
                    final BrainNeuron brainNeuron = new BrainNeuron(genomNeuron);
                    inputMap.put(genomNeuron.getInputId(), brainNeuron);
                    return brainNeuron;
                }).toArray(BrainNeuron[]::new);

        // Output.
        brain.brainOutputArr = genom.genomOutputList.stream().
                map(genomOutput -> {
                    final BrainInputInterface brainInput = inputMap.get(genomOutput.genomInputId);
                    if (Objects.isNull(brainInput)) {
                        throw new RuntimeException(String.format("brainInput with genomInputId \"%d\" for brainOutput in null.", genomOutput.genomInputId));
                    }
                    return new BrainOutput(genomOutput, brainInput);
                }).toArray(BrainOutput[]::new);

        Arrays.stream(brain.brainOutputArr).forEach(brainOutput -> {
            brain.brainOutputMapArr[brainOutput.genomOutput.outputName.ordinal()] = brainOutput;
        });

        // Neuron-Connectors.
        Arrays.stream(brain.brainNeuronArr).forEach(brainNeuron -> {
            final GenomNeuron genomNeuron = brainNeuron.genomNeuron;
            brainNeuron.brainConnectorArr = genomNeuron.genomConnectorList.stream().
                    map(genomConnector -> {
                        final BrainInputInterface brainInput = inputMap.get(genomConnector.genomInputId);
                        if (Objects.isNull(brainInput)) {
                            throw new RuntimeException(String.format("brainInput with genomInputId \"%d\" for brainConnector in null.", genomConnector.genomInputId));
                        }
                        return new BrainConnector(genomConnector, brainInput);
                    }).
                    toArray(BrainConnector[]::new);
        });

        return brain;
    }

    public void calcBrain(final Brain brain) {
        // Sensor Inputs.
        Arrays.stream(brain.brainSensorArr).forEach(brainSensor -> {
            switch (brainSensor.genomSensor.inputName) {
                case Positive -> brainSensor.inValue = 1.0D;
                case Negative -> brainSensor.inValue = -1.0D;
                case Random -> brainSensor.inValue = (this.rnd.nextDouble() - 0.5D) * 2.0D;
                case NeigbourPartTypeAP -> brainSensor.inValue = calcNeigbourPartTypeValue(brain.getNeigbourPartType(Cell.Dir.AP));
                case NeigbourPartTypeAN -> brainSensor.inValue = calcNeigbourPartTypeValue(brain.getNeigbourPartType(Cell.Dir.AN));
                case NeigbourPartTypeBP -> brainSensor.inValue = calcNeigbourPartTypeValue(brain.getNeigbourPartType(Cell.Dir.BP));
                case NeigbourPartTypeBN -> brainSensor.inValue = calcNeigbourPartTypeValue(brain.getNeigbourPartType(Cell.Dir.BN));
                case NeigbourPartTypeCP -> brainSensor.inValue = calcNeigbourPartTypeValue(brain.getNeigbourPartType(Cell.Dir.CP));
                case NeigbourPartTypeCN -> brainSensor.inValue = calcNeigbourPartTypeValue(brain.getNeigbourPartType(Cell.Dir.CN));
                default -> throw new RuntimeException(String.format("Unexpected inputName \"%s\".", brainSensor.genomSensor.inputName));
            }
        });

        // Move Input-Values to Connectors.
        Arrays.stream(brain.brainNeuronArr).forEach(brainNeuron -> {
            Arrays.stream(brainNeuron.brainConnectorArr).forEach(brainConnector -> {
                brainConnector.inValue = brainConnector.brainInput.getOutValue();
            });
        });

        Arrays.stream(brain.brainNeuronArr).forEach(brainNeuron -> {
            brainNeuron.outValue = 0.0D;
            Arrays.stream(brainNeuron.brainConnectorArr).forEach(brainConnector -> {
                brainNeuron.outValue +=
                        brainConnector.inValue *
                        brainConnector.genomConnector.weight +
                        brainConnector.genomConnector.bias;
            });
            brainNeuron.outValue = MathUtils.sigmoid(brainNeuron.outValue);
        });

        // Calculate Outputs.
        Arrays.stream(brain.brainOutputArr).forEach(brainOutput -> {
            brainOutput.outValue = brainOutput.brainInput.getOutValue();
        });
    }

    private double calcNeigbourPartTypeValue(final Part.PartType neigbourPartType) {
        final double value;
        if (Objects.nonNull(neigbourPartType)) {
            value = (neigbourPartType.ordinal() + 1) / (double)(Part.PartType.values().length + 1);
        } else {
            value = 0.0D;
        }
        return value;
    }
}
