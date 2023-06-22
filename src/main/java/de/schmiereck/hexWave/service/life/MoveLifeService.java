package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig;
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
        HexMathUtils.transferVelocityToMove(hexParticle.getVelocityHexVector(), hexParticle.getMoveHexVector());
        final Cell.Dir moveDir = HexMathUtils.determineNextMove(hexParticle.getMoveHexVector());

        if (Objects.nonNull(moveDir)) {
            final Part blockingPart = this.movePart(lifePart, moveDir);
            if (Objects.isNull(blockingPart)) {
                HexMathUtils.calcNextMove3(moveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector());
            } else {
                HexMathUtils.calcNextMove3(moveDir, hexParticle.getMoveHexVector(), hexParticle.velocityHexVector); // TODO ???
                if (blockingPart.getPartType() == Part.PartType.Wall) {
                    HexMathUtils.calcElasticCollisionWithSolidWall(hexParticle, moveDir);
                } else {
                    HexMathUtils.calcElasticCollision(hexParticle, moveDir, blockingPart.getHexParticle());
                }
            }
        }
    }

    public void runMoveOrCollisionWithDirList(final LifePart lifePart) {
        final HexParticle hexParticle = lifePart.getPart().getHexParticle();
        HexMathUtils.transferVelocityToMove(hexParticle.getVelocityHexVector(), hexParticle.getMoveHexVector());
        final List<Cell.Dir> moveDirList = HexMathUtils.determineNextMoveList(hexParticle.getMoveHexVector());

        if (!moveDirList.isEmpty()) {
            Cell.Dir nextMoveDir = null;
            for (final Cell.Dir moveDir : moveDirList) {
                final Part blockingPart = this.checkMovePart(lifePart, moveDir);
                if (Objects.nonNull(blockingPart)) {
                    //HexMathUtils.calcNextMove(moveDir, hexParticle.getMoveHexVector()); // TODO ???
                    //HexMathUtils.calcNextMove3(moveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector()); // TODO ???
                    if (blockingPart.getPartType() == Part.PartType.Wall) {
                        HexMathUtils.calcElasticCollisionWithSolidWall2(hexParticle, moveDir);
                    } else {
                        HexMathUtils.calcElasticCollision(hexParticle, moveDir, blockingPart.getHexParticle());
                    }
                } else {
                    if (Objects.isNull(nextMoveDir)) {
                        nextMoveDir = moveDir;
                    }
                }
            }

            if (Objects.nonNull(nextMoveDir)) {
                HexMathUtils.calcNextMove3(nextMoveDir, hexParticle.getMoveHexVector(), hexParticle.getVelocityHexVector());
                hexParticle.getMoveHexVector().lastCheckedDir = nextMoveDir;

                final Part blockingPart = this.movePart(lifePart, nextMoveDir);

                if (Objects.nonNull(blockingPart)) {
                    throw new RuntimeException("Found unexpected blockingPart while moving.");
                }
            }
        }
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
