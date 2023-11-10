package de.schmiereck.hexWave2.service.life;

import de.schmiereck.hexWave2.MainConfig3;
import de.schmiereck.hexWave2.service.hexGrid.Cell;
import de.schmiereck.hexWave2.service.hexGrid.GridNode;
import de.schmiereck.hexWave2.service.hexGrid.GridNodeArea;
import de.schmiereck.hexWave2.service.hexGrid.HexGridService;
import de.schmiereck.hexWave2.service.hexGrid.Part;
import de.schmiereck.hexWave2.service.hexGrid.PartField;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutputLifeService {
    @Autowired
    private HexGridService hexGridService;

    @Autowired
    private FieldTypeService fieldTypeService;

    private static double calcUsedEnergyValue(final Part part, final double diffValue) {
        final double usedDiffValue;
        if ((part.getEnergy() + diffValue) > MainConfig3.PartMaxEnergy) {
            usedDiffValue = MainConfig3.PartMaxEnergy - part.getEnergy();
        } else {
            usedDiffValue = diffValue;
        }
        return usedDiffValue;
    }

    public void runOutputFields(final List<LifePart> newChildLifePartList, final LifePart lifePart) {
        final Part part = lifePart.getPart();
        final GridNode gridNode = lifePart.getGridNode();

        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part1, lifePart.partIdentity.partIdentity[0], Cell.Dir.AP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part1, lifePart.partIdentity.partIdentity[0], Cell.Dir.AN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part1, lifePart.partIdentity.partIdentity[0], Cell.Dir.BP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part1, lifePart.partIdentity.partIdentity[0], Cell.Dir.BN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part1, lifePart.partIdentity.partIdentity[0], Cell.Dir.CP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part1, lifePart.partIdentity.partIdentity[0], Cell.Dir.CN);

        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part2, lifePart.partIdentity.partIdentity[1], Cell.Dir.AP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part2, lifePart.partIdentity.partIdentity[1], Cell.Dir.AN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part2, lifePart.partIdentity.partIdentity[1], Cell.Dir.BP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part2, lifePart.partIdentity.partIdentity[1], Cell.Dir.BN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part2, lifePart.partIdentity.partIdentity[1], Cell.Dir.CP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part2, lifePart.partIdentity.partIdentity[1], Cell.Dir.CN);

        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part3, lifePart.partIdentity.partIdentity[2], Cell.Dir.AP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part3, lifePart.partIdentity.partIdentity[2], Cell.Dir.AN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part3, lifePart.partIdentity.partIdentity[2], Cell.Dir.BP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part3, lifePart.partIdentity.partIdentity[2], Cell.Dir.BN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part3, lifePart.partIdentity.partIdentity[2], Cell.Dir.CP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.Part3, lifePart.partIdentity.partIdentity[2], Cell.Dir.CN);
    }

    private void runOutputField(final Part part, final GridNode gridNode, final double field, final FieldTypeService.FieldTypeEnum fieldTypeEnum, final Cell.Dir pDir, final Cell.Dir nDir) {
        if (field > 0.1D) {
            this.runOutputField(part, gridNode, fieldTypeEnum, field, pDir);
        } else {
            if (field < -0.1D) {
                this.runOutputField(part, gridNode, fieldTypeEnum, field, nDir);
            }
        }
    }

    private void runOutputField(final Part part, final GridNode gridNode, final FieldTypeService.FieldTypeEnum fieldTypeEnum, final double fieldValue, final Cell.Dir dir) {
        final PartField pushPartField = new PartField(part, this.fieldTypeService.getFieldType(fieldTypeEnum), fieldValue);
        final GridNodeArea gridNodeArea = gridNode.getGridNodeArea(dir, MainConfig3.LifePartOutputFieldStartAreaDistance);
        gridNodeArea.addPartField(pushPartField);
    }

    private void runBirth(final List<LifePart> newChildLifePartList, final LifePart parentLifePart, final GridNode gridNode, final double field, final Cell.Dir pDir, final Cell.Dir nDir) {
        final Optional<LifePart> optionalChildLifePart;
        if (parentLifePart.getPart().getEnergy() > (MainConfig3.InitialLifePartEnergy / 2.0D)) {
            if (field > 0.5D) {
                optionalChildLifePart = runBirth(parentLifePart, gridNode, pDir);
            } else {
                if (field < -0.5D) {
                    optionalChildLifePart = runBirth(parentLifePart, gridNode, nDir);
                } else {
                    optionalChildLifePart = Optional.empty();
                }
            }
        } else {
            optionalChildLifePart = Optional.empty();
        }
        optionalChildLifePart.ifPresent(childLifePart -> newChildLifePartList.add(childLifePart));
    }

    @NotNull
    private Optional<LifePart> runBirth(final LifePart parentLifePart, final GridNode gridNode, final Cell.Dir pDir) {
        final Optional<LifePart> optionalChildLifePart;
        final Optional<GridNode> optionalNeighbourGridNode = this.hexGridService.getEmptyNeighbourGridNode(gridNode, pDir);
        /*
        if (optionalNeighbourGridNode.isPresent()) {
            final Part parentPart = parentLifePart.getPart();

            final double parentPartEnergy = parentPart.getEnergy();
            final double childPartEnergy = parentPartEnergy / 2.0D;
            parentPart.setEnergy(parentPartEnergy - childPartEnergy);

            final LifePart childLifePart = this.birthLifeService.createChildLifePart(parentLifePart,
                    MainConfig3.BirthChildMutationRate,
                    childPartEnergy,
                    optionalNeighbourGridNode.get());
            optionalChildLifePart = Optional.of(childLifePart);
        } else
         */
        {
        optionalChildLifePart = Optional.empty();
        }
        return optionalChildLifePart;
    }

    private Cell.Dir calcMoveDir(double moveA, Cell.Dir pDir, Cell.Dir nDir) {
        final Cell.Dir dir;
        if (moveA > 0.0D) {
            dir = pDir;
        } else {
            if (moveA < 0.0D) {
                dir = nDir;
            } else {
                dir = null;
            }
        }
        return dir;
    }
}
