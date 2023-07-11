package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;
import de.schmiereck.hexWave.utils.HexMathUtils;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoveLifeService {
    @Autowired
    private HexGridService hexGridService;

    public void runMoveOrCollisionWithSingleDir(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();

        HexMathUtils.transferVelocityToMove(hexParticle);
        HexMathUtils.transferAcceleratioToMove(hexParticle);

        final Cell.Dir moveDir = HexMathUtils.determineNextMove(hexParticle.getVMoveHexVector());

        if (Objects.nonNull(moveDir)) {
            final Part blockingPart = this.movePart(lifePart, moveDir);
            if (Objects.isNull(blockingPart)) {
                HexMathUtils.calcNextMove3(moveDir, hexParticle.getVMoveHexVector(), hexParticle.getVelocityHexVector());
            } else {
                HexMathUtils.calcNextMove3(moveDir, hexParticle.getVMoveHexVector(), hexParticle.velocityHexVector); // TODO ???
                if (blockingPart.getPartType() == Part.PartType.Wall) {
                    HexMathUtils.calcVelocityElasticCollisionWithSolidWall(hexParticle, moveDir);
                } else {
                    HexMathUtils.calcVelocityElasticCollision(hexParticle, moveDir, blockingPart.getHexParticle());
                }
            }
        }
    }

    public void runAccelerationAddInToOut(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        //HexMathUtils.runAccelerationSetInToOut(hexParticle);
        HexMathUtils.runAccelerationAddInToOut(hexParticle);
    }
/*
    public void runOutAccelerationToVelocity(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        HexMathUtils.calcOutAccelerationToVelocity(hexParticle);
    }
 */
    public void runMoveOrCollisionWithDirList(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();

        HexMathUtils.transferVelocityToMove(hexParticle);
        HexMathUtils.transferAcceleratioToMove(hexParticle);

        final List<Cell.Dir> aMoveDirList = HexMathUtils.determineNextAMoveList(hexParticle);

        if (!aMoveDirList.isEmpty()) {
            //Cell.Dir nextMoveDir = null;
            for (final Cell.Dir moveDir : aMoveDirList) {
                final Part blockingPart = this.checkMovePart(lifePart, moveDir);
                if (Objects.nonNull(blockingPart)) {
                    if (blockingPart.getPartType() == Part.PartType.Wall) {
                        HexMathUtils.clearAMove(hexParticle, moveDir);
                    } else {
                    }
                } else {
                    HexMathUtils.transferAMoveToVMove(hexParticle, moveDir);
                }
            }
        }

        final List<Cell.Dir> moveDirList = HexMathUtils.determineNextMoveList(hexParticle);

        if (!moveDirList.isEmpty()) {
            Cell.Dir nextMoveDir = null;
            for (final Cell.Dir moveDir : moveDirList) {
                final Part blockingPart = this.checkMovePart(lifePart, moveDir);
                if (Objects.nonNull(blockingPart)) {
                    //HexMathUtils.calcNextMove(moveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector()); // TODO ???
                    //HexMathUtils.calcNextMove2(moveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector()); // TODO ???
                    //HexMathUtils.calcNextMove3(moveDir, hexParticle.getVMoveHexVector(), hexParticle.getVelocityHexVector()); // TODO ???
                    if (blockingPart.getPartType() == Part.PartType.Wall) {
                        //HexMathUtils.calcNextMove22(moveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector());
                        HexMathUtils.calcVelocityElasticCollisionWithSolidWall2(hexParticle, moveDir);
                        HexMathUtils.clearAMove(hexParticle, moveDir);
                        //HexMathUtils.calcNextMove22(moveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector()); // TODO ???
                        //HexMathUtils.calcVelocityElasticCollisionWithSolidWall(hexParticle, moveDir);
                        //HexMathUtils.calcAccelerationElasticCollisionWithSolidWall2(hexParticle, moveDir);
                    } else {
                        HexMathUtils.calcVelocityElasticCollision(hexParticle, moveDir, blockingPart.getHexParticle());
                        //HexMathUtils.calcAccelerationElasticCollision(hexParticle, moveDir, blockingPart.getHexParticle());
                    }
                    //HexMathUtils.calcNextMove(moveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector()); // TODO ???
                    //HexMathUtils.calcNextMove2(moveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector()); // TODO ???
                    //HexMathUtils.calcNextMove3(moveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector()); // TODO ???
                } else {
                    if (Objects.isNull(nextMoveDir)) {
                        nextMoveDir = moveDir;
                    }
                }
            }

            if (Objects.nonNull(nextMoveDir)) {
                HexMathUtils.transferVMoveToVelocity(hexParticle, nextMoveDir);

                HexMathUtils.calcNextMove3(nextMoveDir, hexParticle.getVMoveHexVector(), hexParticle.getVelocityHexVector());
                hexParticle.getVMoveHexVector().lastCheckedDir = nextMoveDir;
System.out.print("nextMoveDir(" + nextMoveDir + ") ");
                final Part blockingPart = this.movePart(lifePart, nextMoveDir);

                if (Objects.nonNull(blockingPart)) {
                    throw new RuntimeException("Found unexpected blockingPart while moving.");
                }
            }
        }
        //hexParticle.getOutAccelerationHexVector().reset();
    }

    private Part movePart(final LifePart lifePart, final Cell.Dir dir) {
        final Part blockingPart;
        final GridNode gridNode = lifePart.getGridNode();
        final GridNode newGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
        if (this.hexGridService.getPartList(newGridNode).isEmpty()) {
            final Part part = lifePart.getPart();
            this.hexGridService.removePart(gridNode, part);
            this.hexGridService.addPart(newGridNode, part);
            lifePart.setGridNode(newGridNode);
            blockingPart = null;
        } else {
            blockingPart = this.hexGridService.getPartList(newGridNode).get(0);
        }
        return blockingPart;
    }

    private Part checkMovePart(final LifePart lifePart, final Cell.Dir dir) {
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
