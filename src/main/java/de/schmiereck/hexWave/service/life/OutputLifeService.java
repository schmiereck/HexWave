package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig3;
import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.genom.GenomOutput;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.GridNodeArea;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.service.hexGrid.PartField;
import de.schmiereck.hexWave.utils.HexMathUtils;
import de.schmiereck.hexWave.utils.MathUtils;

import java.util.List;
import java.util.Objects;
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

    @Autowired
    private BirthLifeService birthLifeService;

    public void runEatOrGiveNeighbourEnergy(final LifePart lifePart) {
        // Output: Eat the Neighbour-Part or not.
        final GridNode gridNode = lifePart.getGridNode();
        final Brain brain = lifePart.getBrain();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            final double brainOutput = brain.getEatNeighbourOutput(dir);
            if (brainOutput > 0.05D) {
                this.calcEatEnergyFromNeighbour(lifePart, gridNode, dir, brainOutput);
            } else {
                if (brainOutput < -0.05D) {
                    this.calcGiveEnergyToNeighbour(lifePart, gridNode, dir, -brainOutput);
                }
            }
        }
    }

    private void calcEatEnergyFromNeighbour(LifePart lifePart, GridNode gridNode, Cell.Dir dir, double brainOutput) {
        final GridNode neighbourGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
        final List<Part> neighbourPartList = this.hexGridService.getPartList(neighbourGridNode);
        if (!neighbourPartList.isEmpty()) {
            final Part neighbourPart = neighbourPartList.get(0);
            final double neighbourEnergy = neighbourPart.getEnergy();

            final double eatPercent = MathUtils.sigmoid(brainOutput);
            final double eatValue = neighbourEnergy * eatPercent;

            if (eatValue > 0.0D) {
                final Part part = lifePart.getPart();
                final double usedEatValue = calcUsedEnergyValue(part, eatValue);
                if (usedEatValue > 0.0D) {
                    neighbourPart.subEnergy(usedEatValue);

                    part.addEnergy(usedEatValue);
                }
            }
        }
    }

    private void calcGiveEnergyToNeighbour(LifePart lifePart, GridNode gridNode, Cell.Dir dir, double brainOutput) {
        final GridNode neighbourGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
        final List<Part> neighbourPartList = this.hexGridService.getPartList(neighbourGridNode);
        if (!neighbourPartList.isEmpty()) {
            final Part neighbourPart = neighbourPartList.get(0);
            final double energy = neighbourPart.getEnergy();

            final double givePercent = MathUtils.sigmoid(brainOutput);
            final double giveValue = energy * givePercent;

            if (giveValue > 0.0D) {
                final Part part = lifePart.getPart();
                final double usedGiveValue = calcUsedEnergyValue(neighbourPart, giveValue);
                if (usedGiveValue > 0.0D) {
                    neighbourPart.addEnergy(giveValue);

                    part.subEnergy(giveValue);
                }
            }
        }
    }

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
        final Brain brain = lifePart.getBrain();
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

        this.runOutputField(part, gridNode, brain.getOutput(GenomOutput.OutputName.ComFieldA), FieldTypeService.FieldTypeEnum.Com, Cell.Dir.AP, Cell.Dir.AN);
        this.runOutputField(part, gridNode, brain.getOutput(GenomOutput.OutputName.ComFieldB), FieldTypeService.FieldTypeEnum.Com, Cell.Dir.BP, Cell.Dir.BN);
        this.runOutputField(part, gridNode, brain.getOutput(GenomOutput.OutputName.ComFieldC), FieldTypeService.FieldTypeEnum.Com, Cell.Dir.CP, Cell.Dir.CN);

        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPush, brain.getOutput(GenomOutput.OutputName.PushFieldAP) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.AP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPush, brain.getOutput(GenomOutput.OutputName.PushFieldAN) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.AN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPush, brain.getOutput(GenomOutput.OutputName.PushFieldBP) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.BP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPush, brain.getOutput(GenomOutput.OutputName.PushFieldBN) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.BN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPush, brain.getOutput(GenomOutput.OutputName.PushFieldCP) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.CP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPush, brain.getOutput(GenomOutput.OutputName.PushFieldCN) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.CN);

        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPull, brain.getOutput(GenomOutput.OutputName.PullFieldAP) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.AP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPull, brain.getOutput(GenomOutput.OutputName.PullFieldAN) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.AN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPull, brain.getOutput(GenomOutput.OutputName.PullFieldBP) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.BP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPull, brain.getOutput(GenomOutput.OutputName.PullFieldBN) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.BN);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPull, brain.getOutput(GenomOutput.OutputName.PullFieldCP) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.CP);
        this.runOutputField(part, gridNode, FieldTypeService.FieldTypeEnum.PartPull, brain.getOutput(GenomOutput.OutputName.PullFieldCN) * MainConfig3.OutputAccelerationFieldFactor, Cell.Dir.CN);

        if (MainConfig3.useBirthOutput) {
            this.runBirth(newChildLifePartList, lifePart, gridNode, brain.getOutput(GenomOutput.OutputName.BirthA), Cell.Dir.AP, Cell.Dir.AN);
            this.runBirth(newChildLifePartList, lifePart, gridNode, brain.getOutput(GenomOutput.OutputName.BirthB), Cell.Dir.BP, Cell.Dir.BN);
            this.runBirth(newChildLifePartList, lifePart, gridNode, brain.getOutput(GenomOutput.OutputName.BirthC), Cell.Dir.CP, Cell.Dir.CN);
        }
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
        } else {
            optionalChildLifePart = Optional.empty();
        }
        return optionalChildLifePart;
    }


    public void runOutputMoveAcceleration(final LifePart lifePart) {
        final Brain brain = lifePart.getBrain();
        final double moveA = 0.0D;
        final double moveB = 1.0D;
        final double moveC = -1.0D;
        final double moveVelocity;

        final Cell.Dir dir;

        if (Math.abs(moveA) > Math.abs(moveB)) {
            if (Math.abs(moveA) > Math.abs(moveC)) {
                dir = this.calcMoveDir(moveA, Cell.Dir.AP, Cell.Dir.AN);
                moveVelocity = moveA;
            } else {
                dir = this.calcMoveDir(moveC, Cell.Dir.CP, Cell.Dir.CN);
                moveVelocity = moveC;
            }
        } else {
            if (Math.abs(moveB) > Math.abs(moveC)) {
                dir = this.calcMoveDir(moveB, Cell.Dir.BP, Cell.Dir.BN);
                moveVelocity = moveB;
            } else {
                dir = this.calcMoveDir(moveC, Cell.Dir.CP, Cell.Dir.CN);
                moveVelocity = moveC;
            }
        }
        if (Objects.nonNull(dir)) {
            //this.movePart(lifePart, dir);
            final Part part = lifePart.getPart();
            final HexParticle hexParticle = part.getHexParticle();

            HexMathUtils.calcAddVelocity(hexParticle, dir, Math.abs((int)(moveVelocity * 32.0D)));

            if (MainConfig3.useEnergy) lifePart.getPart().addEnergy(-0.01);
        }
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
