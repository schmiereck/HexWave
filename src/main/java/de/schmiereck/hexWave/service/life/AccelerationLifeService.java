package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.math.HexParticle;
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
        /*
        for (final Cell.Dir dir : Cell.Dir.values()) {
            for (int areaDistance = 0; areaDistance < maxAreaDistance; areaDistance++) {
                final int finalAreaDistance = areaDistance;
                final GridNodeArea gridNodeArea = gridNode.getGridNodeArea(dir, finalAreaDistance);
                gridNodeArea.getPartFieldList().stream().forEach(gridNodeAreaPartField -> {
                    if (gridNodeAreaPartField.getPart() != lifePart.getPart()) {
                        if (gridNodeAreaPartField.getFieldType() == partPushFieldType) {
                            final int fieldTypeMaxAreaDistance = partPushFieldType.getMaxAreaDistance();
                            final int velocityDiff = (fieldTypeMaxAreaDistance - finalAreaDistance) * 16;
                            final HexParticle hexParticle = lifePart.getPart().getHexParticle();
                            switch (gridNodeArea.getDir()) {
                                case AP -> hexParticle.velocityHexVector.a += velocityDiff;
                                case AN -> hexParticle.velocityHexVector.a -= velocityDiff;
                                case BP -> hexParticle.velocityHexVector.b += velocityDiff;
                                case BN -> hexParticle.velocityHexVector.b -= velocityDiff;
                                case CP -> hexParticle.velocityHexVector.c += velocityDiff;
                                case CN -> hexParticle.velocityHexVector.c -= velocityDiff;
                            }
                        }
                    }
                });
            }
        }
        */
        final Part part = lifePart.getPart();
        final HexParticle hexParticle = part.getHexParticle();
        final double velArr[] = new double[3];
        final int cntArr[] = new int[3];

        for (final GridNodeAreaRef gridNodeAreaRef : gridNode.getGridNodeAreaRefList()) {
            final GridNodeArea gridNodeArea = gridNodeAreaRef.getGridNodeArea();
            final double refValue = gridNodeAreaRef.getValue();
            gridNodeArea.getPartFieldList().stream().forEach(gridNodeAreaPartField -> {
                if (gridNodeAreaPartField.getPart() != part) {
                    if (gridNodeAreaPartField.getFieldType() == partPushFieldType) {
                        final int fieldTypeMaxAreaDistance = partPushFieldType.getMaxAreaDistance();
                        final int finalAreaDistance = gridNodeArea.getAreaDistance();
                        //final int velocityDiff = (int)((fieldTypeMaxAreaDistance - finalAreaDistance) * (refValue * finalAreaDistance));
                        //final int velocityDiff = (int)((refValue * finalAreaDistance));
                        //final int velocityDiff = (int)((refValue * fieldTypeMaxAreaDistance));
                        //final int velocityDiff = (int)((refValue));
                        final double velocityDiff = ((refValue));
                        /*
                        switch (gridNodeArea.getDir()) {
                            case AP -> hexParticle.velocityHexVector.a += velocityDiff;
                            case AN -> hexParticle.velocityHexVector.a -= velocityDiff;
                            case BP -> hexParticle.velocityHexVector.b += velocityDiff;
                            case BN -> hexParticle.velocityHexVector.b -= velocityDiff;
                            case CP -> hexParticle.velocityHexVector.c += velocityDiff;
                            case CN -> hexParticle.velocityHexVector.c -= velocityDiff;
                        }*/
                        switch (gridNodeArea.getDir()) {
                            case AP -> { velArr[0] += velocityDiff; cntArr[0]++; }
                            case AN -> { velArr[0] -= velocityDiff; cntArr[0]++; }
                            case BP -> { velArr[1] += velocityDiff; cntArr[1]++; }
                            case BN -> { velArr[1] -= velocityDiff; cntArr[1]++; }
                            case CP -> { velArr[2] += velocityDiff; cntArr[2]++; }
                            case CN -> { velArr[2] -= velocityDiff; cntArr[2]++; }
                        }

                    }
                }
            });
        }
        if (cntArr[0] > 0) hexParticle.velocityHexVector.a += Math.round(velArr[0] * 10.0D) / cntArr[0];
        if (cntArr[1] > 0) hexParticle.velocityHexVector.b += Math.round(velArr[1] * 10.0D) / cntArr[1];
        if (cntArr[2] > 0) hexParticle.velocityHexVector.c += Math.round(velArr[2] * 10.0D) / cntArr[2];
    }
}
