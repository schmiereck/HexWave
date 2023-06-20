package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.FieldType;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.GridNodeArea;
import de.schmiereck.hexWave.service.hexGrid.GridNodeAreaRef;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.service.hexGrid.PartField;
import de.schmiereck.hexWave.utils.MathUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InputLifeService {
    @Autowired
    private HexGridService hexGridService;

    private final Random rnd = new Random();

    private FieldType part1FieldType;
    private FieldType part2FieldType;
    private FieldType part3FieldType;
    private FieldType comFieldType;

    public InputLifeService() {
    }

    @PostConstruct
    public void init() {
        this.part1FieldType = this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.Part1);
        this.part2FieldType = this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.Part2);
        this.part3FieldType = this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.Part3);
        this.comFieldType = this.hexGridService.getFieldType(HexGridService.FieldTypeEnum.Com);
    }

    public void calcSensorInputs(final LifePart lifePart) {
        // Sensor: For each direction the Neighbour-Part (Part-Type, Field(?)).
        final GridNode gridNode = lifePart.getGridNode();
        final Brain brain = lifePart.getBrain();
        final Part part = lifePart.getPart();

        // Sensor Inputs.
        Arrays.stream(brain.brainSensorArr).forEach(brainSensor -> {
            switch (brainSensor.genomSensor.inputName) {
                case Positive -> brainSensor.inValue = 1.0D;
                case Negative -> brainSensor.inValue = -1.0D;
                case Random -> brainSensor.inValue = (this.rnd.nextDouble() - 0.5D) * 2.0D;
                case Energy -> brainSensor.inValue = MathUtils.sigmoid(part.getEnergy());
                case NeigbourPartTypeAP -> brainSensor.inValue = this.calcNeigbourPartTypeValue(this.calcNeigbourPartType(gridNode, Cell.Dir.AP));
                case NeigbourPartTypeAN -> brainSensor.inValue = this.calcNeigbourPartTypeValue(this.calcNeigbourPartType(gridNode, Cell.Dir.AN));
                case NeigbourPartTypeBP -> brainSensor.inValue = this.calcNeigbourPartTypeValue(this.calcNeigbourPartType(gridNode, Cell.Dir.BP));
                case NeigbourPartTypeBN -> brainSensor.inValue = this.calcNeigbourPartTypeValue(this.calcNeigbourPartType(gridNode, Cell.Dir.BN));
                case NeigbourPartTypeCP -> brainSensor.inValue = this.calcNeigbourPartTypeValue(this.calcNeigbourPartType(gridNode, Cell.Dir.CP));
                case NeigbourPartTypeCN -> brainSensor.inValue = this.calcNeigbourPartTypeValue(this.calcNeigbourPartType(gridNode, Cell.Dir.CN));

                case Part1FieldAP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.AP, this.part1FieldType);
                case Part1FieldAN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.AN, this.part1FieldType);
                case Part1FieldBP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.BP, this.part1FieldType);
                case Part1FieldBN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.BN, this.part1FieldType);
                case Part1FieldCP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.CP, this.part1FieldType);
                case Part1FieldCN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.CN, this.part1FieldType);

                case Part2FieldAP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.AP, this.part2FieldType);
                case Part2FieldAN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.AN, this.part2FieldType);
                case Part2FieldBP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.BP, this.part2FieldType);
                case Part2FieldBN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.BN, this.part2FieldType);
                case Part2FieldCP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.CP, this.part2FieldType);
                case Part2FieldCN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.CN, this.part2FieldType);

                case Part3FieldAP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.AP, this.part3FieldType);
                case Part3FieldAN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.AN, this.part3FieldType);
                case Part3FieldBP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.BP, this.part3FieldType);
                case Part3FieldBN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.BN, this.part3FieldType);
                case Part3FieldCP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.CP, this.part3FieldType);
                case Part3FieldCN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.CN, this.part3FieldType);

                case ComFieldAP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.AP, this.comFieldType);
                case ComFieldAN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.AN, this.comFieldType);
                case ComFieldBP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.BP, this.comFieldType);
                case ComFieldBN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.BN, this.comFieldType);
                case ComFieldCP -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.CP, this.comFieldType);
                case ComFieldCN -> brainSensor.inValue = this.calcFieldValue(gridNode, Cell.Dir.CN, this.comFieldType);
                default -> throw new RuntimeException(String.format("Unexpected inputName \"%s\".", brainSensor.genomSensor.inputName));
            }
        });
    }

    private double calcFieldValue(final GridNode gridNode, final Cell.Dir dir, final FieldType filterFieldType) {
        double value = 0.0D;
        for (final GridNodeAreaRef gridNodeAreaRef : gridNode.getGridNodeAreaRefList()) {
            final GridNodeArea gridNodeArea = gridNodeAreaRef.getGridNodeArea();
            if (gridNodeArea.getDir() == dir) {
                final double refValue = gridNodeAreaRef.getValue();
                for (final PartField gridNodeAreaPartField : gridNodeArea.getPartFieldList()) {
                    if (gridNodeAreaPartField.getFieldType() == filterFieldType) {
                        value += refValue / gridNodeAreaPartField.getParentAreaDistance();
                    }
                }
            }
        }
        return MathUtils.sigmoid(value);
    }


    @Nullable
    private Part.PartType calcNeigbourPartType(final GridNode gridNode, final Cell.Dir dir) {
        final GridNode neighbourGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
        final List<Part> neighbourPartList = neighbourGridNode.getPartList(this.hexGridService.getActCellArrPos());
        final Part.PartType neigbourPartType;
        if (!neighbourPartList.isEmpty()) {
            final Part neighbourPart = neighbourPartList.get(0);
            neigbourPartType = neighbourPart.getPartType();
        } else {
            neigbourPartType = null;
        }
        return neigbourPartType;
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
