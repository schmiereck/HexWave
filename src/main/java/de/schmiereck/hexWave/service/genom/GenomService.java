package de.schmiereck.hexWave.service.genom;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class GenomService {
    private final Random rnd = new Random();

    public Genom createInitialGenom() {
        final Genom genom = new Genom();

        //genom.genomOutputList.add(new GenomOutput(0, GenomOutput.OutputName.MoveA));
        //genom.genomOutputList.add(new GenomOutput(1, GenomOutput.OutputName.MoveB));
        //genom.genomOutputList.add(new GenomOutput(2, GenomOutput.OutputName.MoveC));

        //final GenomNeuron genomNeuron = new GenomNeuron(3);
        //genomNeuron.genomConnectorList.add(new GenomConnector(4, 0.1D, 0.2D));
        //genom.genomNeuronList.add(genomNeuron);

        return genom;
    }

    public Genom createSunGenom() {
        final Genom genom = new Genom();

        //genom.genomSensorList.add(new GenomSensor(0, GenomSensor.InputName.Negative));
        //genom.genomOutputList.add(new GenomOutput(0, GenomOutput.OutputName.MoveA));
        //genom.genomOutputList.add(new GenomOutput(1, GenomOutput.OutputName.MoveB));
        //genom.genomOutputList.add(new GenomOutput(1, 0, GenomOutput.OutputName.MoveC));

        //final GenomNeuron genomNeuron = new GenomNeuron(3);
        //genomNeuron.genomConnectorList.add(new GenomConnector(4, 0.1D, 0.2D));
        //genom.genomNeuronList.add(genomNeuron);

        return genom;
    }

    public Genom createMutatedGenom(final Genom genom, final double mutationRate) {
        final Genom newGenom = new Genom();
        newGenom.nextId = genom.nextId;

        // Input-Sensor
        this.copyMutatedGenomSensorList(genom, newGenom, mutationRate);
        this.addRandomenomSensor(genom, newGenom, mutationRate);

        // Neuron & Connector
        this.copyMutatedGenomNeuronList(genom, newGenom, mutationRate);
        this.addRandomGenomNeuron(genom, newGenom, mutationRate);
        this.addRandomGenomConnector(genom, newGenom, mutationRate);

        // Output
        this.copyMutatedGenomOutputList(genom, newGenom, mutationRate);
        this.addRandomGenomOutput(genom, newGenom, mutationRate);

        return newGenom;
    }

    private void copyMutatedGenomNeuronList(final Genom genom, final Genom newGenom, final double mutationRate) {
        // Copy neurons.
        for (int neuronPos = 0; neuronPos < genom.genomNeuronList.size(); neuronPos++) {
            final GenomNeuron genomNeuron = genom.genomNeuronList.get(neuronPos);

            // Copy Neuron?
            if (mutateOften(mutationRate)) {
                final GenomNeuron newGenomNeuron;

                // Mutate Neuron?
                if (mutateRarely(mutationRate)) {
                    newGenomNeuron = this.copyMutatedGenomNeuron(genom, genomNeuron, mutationRate);
                } else {
                    newGenomNeuron = this.copyGenomNeuron(genom, genomNeuron);
                }

                addGenomNeuronInput(newGenom, newGenomNeuron);
            }
        }
        // Copy Connectors.
        newGenom.genomNeuronList.stream().forEach(newGenomNeuron -> {
            final GenomNeuron genomNeuron = (GenomNeuron) genom.genomInputMap.get(newGenomNeuron.getInputId());

            // Mutate Neuron-Connector?
            if (mutateVeryOften(mutationRate)) {
                this.copyMutatedGenomNeuronConnectorList(newGenom, genomNeuron, newGenomNeuron, mutationRate);
            } else {
                this.copyGenomNeuronConnectorList(newGenom, genomNeuron, newGenomNeuron);
            }
        });

        // Remove Connectors with no input.
        //newGenom.genomNeuronList.stream().forEach(genomNeuron -> {
        //    final List<GenomConnector> removedGenomConnectorList = genomNeuron.genomConnectorList.stream().filter(genomConnector ->
        //            Objects.isNull(newGenom.genomInputMap.get(genomConnector.genomInputId))
        //    ).collect(Collectors.toList());
        //    genomNeuron.genomConnectorList.removeAll(removedGenomConnectorList);
        //});
    }

    private void addRandomGenomConnector(final Genom genom, final Genom newGenom, final double mutationRate) {
        if (this.mutateVeryOften(mutationRate)) {
            if (!newGenom.genomNeuronList.isEmpty()) {
                final GenomNeuron genomNeuron = newGenom.genomNeuronList.get(this.rnd.nextInt(newGenom.genomNeuronList.size()));

                final Optional<GenomConnector> optionalGenomConnector = this.createRandomGenomConnector(newGenom);

                optionalGenomConnector.ifPresent(genomConnector -> genomNeuron.genomConnectorList.add(genomConnector));
            }
        }
    }


    private void addRandomGenomNeuron(final Genom genom, final Genom newGenom, final double mutationRate) {
        // Add Neuron?
        if (this.mutateOften(mutationRate)) {
            final GenomNeuron newGenomNeuron = this.createGenomNeuron(genom, newGenom);
            addGenomNeuronInput(newGenom, newGenomNeuron);
        }
    }

    private void addRandomGenomOutput(final Genom genom, final Genom newGenom, final double mutationRate) {
        // Add Output?
        if (this.mutateRarely(mutationRate)) {
            final Optional<GenomOutput> newGenomOutput = this.createRandomGenomOutput(genom, newGenom);
            newGenomOutput.ifPresent(genomOutput -> addGenomNeuronOutput(newGenom, genomOutput));
        }
    }

    private static void addGenomNeuronInput(Genom newGenom, GenomNeuron newGenomNeuron) {
        newGenom.genomNeuronList.add(newGenomNeuron);
        newGenom.genomInputMap.put(newGenomNeuron.getInputId(), newGenomNeuron);
    }

    private GenomNeuron createGenomNeuron(final Genom genom, final Genom newGenom) {
        final GenomNeuron newGenomNeuron = new GenomNeuron(this.calcNextId(newGenom));

        if (!newGenom.genomInputMap.isEmpty()) {
            final GenomInputInterface genomInput = this.calcRandomGenomInput(newGenom);

            final GenomConnector newGenomConnector = this.createRandomGenomConnector(genomInput);

            newGenomNeuron.genomConnectorList.add(newGenomConnector);
        }

        return newGenomNeuron;
    }

    private GenomInputInterface calcRandomGenomInput(Genom genom) {
        final Object[] genomInputKeyArr = genom.genomInputMap.keySet().toArray();
        final Integer inputId = (Integer)genomInputKeyArr[this.rnd.nextInt(genomInputKeyArr.length)];
        final GenomInputInterface genomInput = genom.genomInputMap.get(inputId);
        return genomInput;
    }

    private GenomNeuron copyGenomNeuron(final Genom genom, final GenomNeuron genomNeuron) {
        final GenomNeuron newGenomNeuron = new GenomNeuron(genomNeuron.neuronId);

        //this.copyGenomNeuronConnectorList(genomNeuron, newGenomNeuron);

        return newGenomNeuron;
    }

    private void copyGenomNeuronConnectorList(final Genom newGenom, final GenomNeuron genomNeuron, final GenomNeuron newGenomNeuron) {
        newGenomNeuron.genomConnectorList.addAll(genomNeuron.genomConnectorList.stream().
                filter(genomConnector -> Objects.nonNull(newGenom.genomInputMap.get(genomConnector.genomInputId))).
                map(genomConnector -> new GenomConnector(genomConnector.genomInputId, genomConnector.weight, genomConnector.bias)).
                collect(Collectors.toList()));
    }

    private GenomNeuron copyMutatedGenomNeuron(final Genom genom, final GenomNeuron genomNeuron, final double mutationRate) {
        final GenomNeuron newGenomNeuron = new GenomNeuron(genomNeuron.neuronId);

        //this.copyMutatedGenomNeuronConnectorList(genom, genomNeuron, newGenomNeuron, mutationRate);

        return newGenomNeuron;
    }

    private void copyMutatedGenomNeuronConnectorList(final Genom newGenom, final GenomNeuron genomNeuron, final GenomNeuron newGenomNeuron, final double mutationRate) {
        newGenomNeuron.genomConnectorList.addAll(genomNeuron.genomConnectorList.stream().
                filter(genomConnector -> this.mutateRarely(mutationRate)).
                filter(genomConnector -> Objects.nonNull(newGenom.genomInputMap.get(genomConnector.genomInputId))).
                map(genomConnector -> new GenomConnector(genomConnector.genomInputId,
                        this.calcMutatedValue(genomConnector.weight, mutationRate),
                        this.calcMutatedValue(genomConnector.bias, mutationRate))).
                collect(Collectors.toList()));

        // Add Connector?
        if (this.mutateRarely(mutationRate)) {
            final Optional<GenomConnector> newGenomConnector = this.createRandomGenomConnector(newGenom);
            newGenomConnector.ifPresent(genomConnector -> newGenomNeuron.genomConnectorList.add(genomConnector));
        }
    }

    private void copyMutatedGenomOutputList(Genom genom, Genom newGenom, double mutationRate) {
        // Copy Outputs.
        for (int outputPos = 0; outputPos < genom.genomOutputList.size(); outputPos++) {
            // Copy Output?
            if (this.mutateOften(mutationRate)) {
                final GenomOutput genomOutput = genom.genomOutputList.get(outputPos);
                final GenomOutput newGenomOutput;

                // Mutate Output?
                if (this.mutateRarely(mutationRate)) {
                    newGenomOutput = this.copyMutatedGenomOutput(genom, genomOutput, mutationRate);
                } else {
                    newGenomOutput = this.copyGenomOutput(genom, genomOutput);
                }

                addGenomNeuronOutput(newGenom, newGenomOutput);
            }
        }
        // Remove Outputs with no input.
        final List<GenomOutput> removedGenomOutputList = newGenom.genomOutputList.stream().filter(genomOutput ->
                Objects.isNull(newGenom.genomInputMap.get(genomOutput.genomInputId))
            ).collect(Collectors.toList());

        newGenom.genomOutputList.removeAll(removedGenomOutputList);
    }

    private GenomOutput copyMutatedGenomOutput(final Genom genom, final GenomOutput genomOutput, final double mutationRate) {
        final int outputId = genomOutput.outputId;
        final int genomInputId;
        final GenomOutput.OutputName outputName;

        if (this.mutateRarely(mutationRate)) {
            if (!genom.genomInputMap.isEmpty()) {
                genomInputId = this.calcRandomGenomInput(genom).getInputId();
            } else {
                genomInputId = genomOutput.genomInputId;
            }
        } else {
            genomInputId = genomOutput.genomInputId;
        }

        if (this.mutateRarely(mutationRate)) {
            outputName = this.calcRandomOutputName();
        } else {
            outputName = genomOutput.outputName;
        }

        final GenomOutput newGenomOutput = new GenomOutput(outputId, genomInputId, outputName);
        return newGenomOutput;
    }

    private GenomOutput.OutputName calcRandomOutputName() {
        final GenomOutput.OutputName outputName = GenomOutput.OutputName.values()[this.rnd.nextInt(GenomOutput.OutputName.values().length)];
        return outputName;
    }

    private Optional<GenomOutput> createRandomGenomOutput(final Genom genom, final Genom newGenom) {
        final Optional<GenomOutput> newGenomOutput;

        if (!newGenom.genomInputMap.isEmpty()) {
            final GenomInputInterface genomInput = this.calcRandomGenomInput(newGenom);

            final GenomOutput.OutputName outputName = this.calcRandomOutputName();

            newGenomOutput = Optional.of(new GenomOutput(this.calcNextId(newGenom), genomInput.getInputId(), outputName));
        } else {
            newGenomOutput = Optional.empty();
        }

        return newGenomOutput;
    }

    private static void addGenomNeuronOutput(final Genom newGenom, final GenomOutput newGenomOutput) {
        newGenom.genomOutputList.add(newGenomOutput);
    }

    private void copyMutatedGenomSensorList(Genom genom, Genom newGenom, double mutationRate) {
        // Copy sensors.
        for (int sensorPos = 0; sensorPos < genom.genomSensorList.size(); sensorPos++) {
            // Copy Sensor?
            if (this.mutateOften(mutationRate)) {
                final GenomSensor genomSensor = genom.genomSensorList.get(sensorPos);
                final GenomSensor newGenomSensor;

                // Mutate Sensor?
                if (this.mutateRarely(mutationRate)) {
                    newGenomSensor = this.copyMutatedGenomSensor(genom, genomSensor, mutationRate);
                } else {
                    newGenomSensor = this.copyGenomSensor(genom, genomSensor);
                }

                addGenomNeuronSensor(newGenom, newGenomSensor);
            }
        }
    }

    private GenomSensor copyMutatedGenomSensor(final Genom genom, final GenomSensor genomSensor, final double mutationRate) {
        final int sensorId = genomSensor.sensorId;
        final GenomSensor.InputName inputName;

        if (this.mutateRarely(mutationRate)) {
            inputName = this.calcRandomSensorName();
        } else {
            inputName = genomSensor.inputName;
        }

        final GenomSensor newGenomSensor = new GenomSensor(sensorId, inputName);
        return newGenomSensor;
    }

    private GenomSensor.InputName calcRandomSensorName() {
        final GenomSensor.InputName inputName = GenomSensor.InputName.values()[this.rnd.nextInt(GenomSensor.InputName.values().length)];
        return inputName;
    }

    private GenomSensor copyGenomSensor(final Genom genom, final GenomSensor genomSensor) {
        final GenomSensor newGenomSensor = new GenomSensor(genomSensor.sensorId, genomSensor.inputName);
        return newGenomSensor;
    }

    private void addRandomenomSensor(final Genom genom, final Genom newGenom, final double mutationRate) {
        // Add Sensor?
        if (this.mutateRarely(mutationRate)) {
            final Optional<GenomSensor> newGenomSensor = this.createRandomGenomSensor(genom, newGenom);
            newGenomSensor.ifPresent(genomSensor -> addGenomNeuronSensor(newGenom, genomSensor));
        }
    }

    private Optional<GenomSensor> createRandomGenomSensor(final Genom genom, final Genom newGenom) {
        final Optional<GenomSensor> newGenomSensor;

        //if (!genom.genomInputMap.isEmpty()) {
            final GenomSensor.InputName inputName = this.calcRandomSensorName();

            newGenomSensor = Optional.of(new GenomSensor(this.calcNextId(newGenom), inputName));
        //} else {
        //    newGenomSensor = Optional.empty();
        //}

        return newGenomSensor;
    }

    private static void addGenomNeuronSensor(final Genom newGenom, final GenomSensor newGenomSensor) {
        newGenom.genomSensorList.add(newGenomSensor);
        newGenom.genomInputMap.put(newGenomSensor.getInputId(), newGenomSensor);
    }

    private boolean mutateRarely(final double mutationRate) {
        return this.rnd.nextDouble() < mutationRate;
    }

    private boolean mutateOften(final double mutationRate) {
        return this.rnd.nextDouble() < (mutationRate * 2.0D);
    }

    private boolean mutateVeryOften(final double mutationRate) {
        return this.rnd.nextDouble() < (mutationRate * 4.0D);
    }

    private GenomOutput copyGenomOutput(final Genom genom, final GenomOutput genomOutput) {
        final GenomOutput newGenomOutput = new GenomOutput(genomOutput.outputId, genomOutput.genomInputId, genomOutput.outputName);
        return newGenomOutput;
    }

    private Optional<GenomConnector> createRandomGenomConnector(final Genom newGenom) {
        final Optional<GenomConnector> newGenomConnector;

        if (!newGenom.genomInputMap.isEmpty()) {
            final GenomInputInterface genomInput = this.calcRandomGenomInput(newGenom);

            newGenomConnector = Optional.of(this.createRandomGenomConnector(genomInput));
        } else {
            newGenomConnector = Optional.empty();
        }
        return newGenomConnector;
    }

    private GenomConnector createRandomGenomConnector(final GenomInputInterface genomInput) {
        final GenomConnector newGenomConnector = new GenomConnector(genomInput.getInputId(),
                this.calcNewValue(), this.calcNewValue());
        return newGenomConnector;
    }

    private double calcMutatedValue(final double value, final double mutationRate) {
        return value + ((this.rnd.nextDouble() - 0.5D) * mutationRate);
    }

    private double calcNewValue() {
        return this.rnd.nextDouble() - 0.5D;
    }

    public int calcNextId(final Genom genom) {
        final int id = genom.nextId;
        genom.nextId = id + 1;
        return id;
    }
}
