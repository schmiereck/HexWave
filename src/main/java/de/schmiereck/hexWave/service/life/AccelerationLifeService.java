package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.FieldType;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.GridNodeArea;
import de.schmiereck.hexWave.service.hexGrid.GridNodeAreaRef;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;

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
        hexParticle.velocityHexVector.c -= 1;
        hexParticle.velocityHexVector.b += 1;
    }

    public void calcFieldAcceleration(final LifePart lifePart) {
        final FieldType partPushFieldType = this.fieldTypeService.getFieldType(FieldTypeService.FieldTypeEnum.PartPush);
        final int maxAreaDistance = this.hexGridService.getMaxAreaDistance();
        final GridNode gridNode = lifePart.getGridNode();

        final Part part = lifePart.getPart();
        final HexParticle partHexParticle = part.getHexParticle();

        for (final GridNodeAreaRef gridNodeAreaRef : gridNode.getGridNodeAreaRefList()) {
            final GridNodeArea gridNodeArea = gridNodeAreaRef.getGridNodeArea();
            final double refValue = gridNodeAreaRef.getValue();
            gridNodeArea.getPartFieldList().stream().forEach(gridNodeAreaPartField -> {
                final Part otherPart = gridNodeAreaPartField.getPart();
                if (otherPart != part) {
                    final int fieldDirection;
                    if (gridNodeAreaPartField.getFieldType() == partPushFieldType) {
                        fieldDirection = 1;
                    } else {
                        if (gridNodeAreaPartField.getFieldType() == partPushFieldType) {
                            fieldDirection = -1;
                        } else {
                            fieldDirection = 0;
                        }
                    }
                    if (fieldDirection != 0) {
                        final HexParticle otherPartHexParticle = otherPart.getHexParticle();
                        final int fieldTypeMaxAreaDistance = partPushFieldType.getMaxAreaDistance();
                        final int finalAreaDistance = gridNodeArea.getAreaDistance();
                        //final int velocityDiff = (int)((fieldTypeMaxAreaDistance - finalAreaDistance) * (refValue * finalAreaDistance));
                        //final int velocityDiff = (int)((refValue * finalAreaDistance));
                        //final int velocityDiff = (int)((refValue * fieldTypeMaxAreaDistance));
                        //final int velocityDiff = (int)((refValue));
                        final double velocityDiff = ((refValue));
                        final long velocityDiffValue = Math.round(velocityDiff * MainConfig.FieldVelocityDiffFactor) * fieldDirection;

                        final Cell.Dir gridNodeAreaDir = gridNodeArea.getDir();

                        addVelocityDiffValue(gridNodeAreaDir, partHexParticle, velocityDiffValue);
                        addVelocityDiffValue(gridNodeAreaDir, otherPartHexParticle, -velocityDiffValue);
                    }
                }
            });
        }
    }

    private static void addVelocityDiffValue(final Cell.Dir gridNodeAreaDir, final HexParticle partHexParticle, final long velocityDiffValue) {
        switch (gridNodeAreaDir) {
            case AP -> { partHexParticle.velocityHexVector.a += velocityDiffValue; }
            case AN -> { partHexParticle.velocityHexVector.a -= velocityDiffValue; }
            case BP -> { partHexParticle.velocityHexVector.b += velocityDiffValue; }
            case BN -> { partHexParticle.velocityHexVector.b -= velocityDiffValue; }
            case CP -> { partHexParticle.velocityHexVector.c += velocityDiffValue; }
            case CN -> { partHexParticle.velocityHexVector.c -= velocityDiffValue; }
        }
    }
}
