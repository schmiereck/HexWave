package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.MainConfig;
import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.genom.GenomOutput;
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
public class OutputLifeService {
    @Autowired
    private HexGridService hexGridService;

    public void runEatNeighbour(final LifePart lifePart) {
        // Output: Eat the Neighbour-Part or not.
        final GridNode gridNode = lifePart.getGridNode();
        final Brain brain = lifePart.getBrain();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            final double brainOutput = brain.getEatNeighbourOutput(dir);
            if (brainOutput > 0.5D) {
                final GridNode neighbourGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
                final List<Part> neighbourPartList = neighbourGridNode.getPartList(this.hexGridService.getActCellArrPos());
                if (!neighbourPartList.isEmpty()) {
                    final Part neighbourPart = neighbourPartList.get(0);
                    final double energy = neighbourPart.getEnergy();
                    neighbourPart.setEnergy(0.0D);

                    if (energy > 0.0D) {
                        final Part part = lifePart.getPart();
                        part.addEnergy(energy);
                    }
                }
            }
        }
    }

    public void runOutputFields(final LifePart lifePart) {

    }

    public void runMoveAcceleration(final LifePart lifePart) {
        final Brain brain = lifePart.getBrain();
        final double moveA = brain.getOutput(GenomOutput.OutputName.MoveA);
        final double moveB = brain.getOutput(GenomOutput.OutputName.MoveB);
        final double moveC = brain.getOutput(GenomOutput.OutputName.MoveC);

        final Cell.Dir dir;

        if (Math.abs(moveA) > Math.abs(moveB)) {
            if (Math.abs(moveA) > Math.abs(moveC)) {
                dir = this.calcMoveDir(moveA, Cell.Dir.AP, Cell.Dir.AN);
            } else {
                dir = this.calcMoveDir(moveC, Cell.Dir.CP, Cell.Dir.CN);
            }
        } else {
            if (Math.abs(moveB) > Math.abs(moveC)) {
                dir = this.calcMoveDir(moveB, Cell.Dir.BP, Cell.Dir.BN);
            } else {
                dir = this.calcMoveDir(moveC, Cell.Dir.CP, Cell.Dir.CN);
            }
        }
        if (Objects.nonNull(dir)) {
            //this.movePart(lifePart, dir);
            final Part part = lifePart.getPart();
            final HexParticle hexParticle = part.getHexParticle();

            HexMathUtils.calcAddVelocity(hexParticle, dir, 32);

            if (MainConfig.useEnergy) lifePart.getPart().addEnergy(-0.01);
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
