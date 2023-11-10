package de.schmiereck.hexWave2.service.life;

import de.schmiereck.hexWave2.MainConfig3;
import de.schmiereck.hexWave2.math.HexParticle;
import de.schmiereck.hexWave2.service.hexGrid.Cell;
import de.schmiereck.hexWave2.service.hexGrid.FieldType;
import de.schmiereck.hexWave2.service.hexGrid.GridNode;
import de.schmiereck.hexWave2.service.hexGrid.GridNodeArea;
import de.schmiereck.hexWave2.service.hexGrid.GridNodeAreaRef;
import de.schmiereck.hexWave2.service.hexGrid.HexGridService;
import de.schmiereck.hexWave2.service.hexGrid.Part;
import de.schmiereck.hexWave2.utils.HexMathUtils;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccelerationLifeService {
    @Autowired
    private HexGridService hexGridService;

    @Autowired
    private FieldTypeService fieldTypeService;

    public void calcGravitationalAcceleration(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        HexMathUtils.calcAddOutAcceleration(hexParticle, Cell.Dir.BP, MainConfig3.GravitationalAccelerationBP);
        HexMathUtils.calcAddOutAcceleration(hexParticle, Cell.Dir.CN, MainConfig3.GravitationalAccelerationCN);
        //hexParticle.velocityHexVector.b += MainConfig3.GravitationalAccelerationBP;
        //hexParticle.velocityHexVector.c -= MainConfig3.GravitationalAccelerationCN;
    }

    public void calcFieldOutAcceleration(final LifePart lifePart) {
        final FieldType partPushFieldType = this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.PartPush);
        final FieldType partPullFieldType = this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.PartPull);
        final int maxAreaDistance = this.hexGridService.getMaxAreaDistance();
        final GridNode gridNode = lifePart.getGridNode();

        final Part part = lifePart.getPart();
        final HexParticle partHexParticle = part.getHexParticle();

        for (final GridNodeAreaRef gridNodeAreaRef : gridNode.getGridNodeAreaRefList()) {
            final GridNodeArea gridNodeArea = gridNodeAreaRef.getGridNodeArea();
            gridNodeArea.getPartFieldList().stream().forEach(gridNodeAreaPartField -> {
                final Part otherPart = gridNodeAreaPartField.getPart();
                if (otherPart != part) {
                    final int fieldDirection;
                    if (gridNodeAreaPartField.getFieldType() == partPushFieldType) {
                        fieldDirection = 1;
                    } else {
                        if (gridNodeAreaPartField.getFieldType() == partPullFieldType) {
                            fieldDirection = -1;
                        } else {
                            fieldDirection = 0;
                        }
                    }
                    if (fieldDirection != 0) {
                        final HexParticle otherPartHexParticle = otherPart.getHexParticle();
                        final double refValue = gridNodeAreaRef.getValue();
                        final int fieldTypeMaxAreaDistance = partPushFieldType.getMaxAreaDistance();
                        final int finalAreaDistance = gridNodeArea.getAreaDistance();
                        //final int velocityDiff = (int)((fieldTypeMaxAreaDistance - finalAreaDistance) * (refValue * finalAreaDistance));
                        //final int velocityDiff = (int)((refValue * finalAreaDistance));
                        //final int velocityDiff = (int)((refValue * fieldTypeMaxAreaDistance));
                        //final int velocityDiff = (int)((refValue));
                        final double velocityDiff = ((refValue) * gridNodeAreaPartField.getValue());
                        final int velocityDiffValue = (int)Math.round(velocityDiff * MainConfig3.FieldVelocityDiffFactor) * fieldDirection;

                        final Cell.Dir gridNodeAreaDir = gridNodeArea.getDir();
                        //final Cell.Dir oppositeGridNodeAreaDir = DirUtils.calcOppositeDir(gridNodeAreaDir);
                        addOutAccellerationDiffValue(gridNodeAreaDir, partHexParticle, velocityDiffValue);
                        addOutAccellerationDiffValue(gridNodeAreaDir, otherPartHexParticle, -velocityDiffValue);
                        //addInAccellerationDiffValue(oppositeGridNodeAreaDir, otherPartHexParticle, velocityDiffValue);
                    }
                }
            });
        }
    }

    private static void addOutAccellerationDiffValue(final Cell.Dir gridNodeAreaDir, final HexParticle partHexParticle, final int velocityDiffValue) {
        HexMathUtils.calcAddOutAcceleration(partHexParticle, gridNodeAreaDir, velocityDiffValue);
    }

    public void calcAddOutAccelerationToNeigboursIn(final LifePart lifePart) {
        final Part part = lifePart.getPart();
        final HexParticle hexParticle = part.getHexParticle();

        for (final Cell.Dir dir : Cell.Dir.values()) {
            final Part blockingPart = this.checkAccelerationPart(lifePart, dir);
            final int outAcceleration = HexMathUtils.calcGetOutAcceleration(hexParticle, dir);
            if (outAcceleration > 0) {
                // TODO ??? Only if Velocity / Move in this Direction?
                if (Objects.nonNull(blockingPart)) {
                    final HexParticle blockingPartHexParticle = blockingPart.getHexParticle();
                    HexMathUtils.calcAddInAcceleration(blockingPartHexParticle, dir, outAcceleration);
                    //HexMathUtils.calcSetOutAcceleration(hexParticle, dir, 0);
                }
            }
        }
    }

    public void calcOutAccelerationToVelocity(final LifePart lifePart) {
        final Part part = lifePart.getPart();
        final HexParticle hexParticle = part.getHexParticle();

        for (final Cell.Dir dir : Cell.Dir.values()) {
            final Part blockingPart = this.checkAccelerationPart(lifePart, dir);
            //final Part blockingPart = this.checkVelocityPart(lifePart, dir);
            final int outAcceleration = HexMathUtils.calcGetOutAcceleration(hexParticle, dir);
            if (outAcceleration > 0) {
                if (Objects.isNull(blockingPart)) {
                    final int velocity = outAcceleration;// / hexParticle.getMass();

                    HexMathUtils.calcAddVelocity(hexParticle, dir, velocity);
                }
                HexMathUtils.calcSetOutAcceleration(hexParticle, dir, 0);
            }
        }
    }

    public void calcClearOutAcceleration(final LifePart lifePart) {
        final Part part = lifePart.getPart();
        final HexParticle hexParticle = part.getHexParticle();

        for (final Cell.Dir dir : Cell.Dir.values()) {
            HexMathUtils.calcSetOutAcceleration(hexParticle, dir, 0);
        }
    }

    private Part checkAccelerationPart(final LifePart lifePart, final Cell.Dir dir) {
        final Part blockingPart;
        final GridNode gridNode = lifePart.getGridNode();
        final GridNode newGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
        if (this.hexGridService.getPartList(newGridNode).isEmpty()) {
            blockingPart = null;
        } else {
            blockingPart = this.hexGridService.getPartList(newGridNode).get(0);
        }
        return blockingPart;
    }

    private Part checkVelocityPart(final LifePart lifePart, final Cell.Dir dir) {
        final Part blockingPart;
        final GridNode gridNode = lifePart.getGridNode();
        final GridNode newGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
        if (this.hexGridService.getPartList(newGridNode).isEmpty()) {
            blockingPart = null;
        } else {
            blockingPart = this.hexGridService.getPartList(newGridNode).get(0);
        }
        return blockingPart;
    }
}
