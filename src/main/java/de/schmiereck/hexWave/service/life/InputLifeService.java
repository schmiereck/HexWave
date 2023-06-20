package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.hexGrid.Cell;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;
import de.schmiereck.hexWave.service.hexGrid.Part;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InputLifeService {
    @Autowired
    private HexGridService hexGridService;

    public void runNeighbourSensors(final LifePart lifePart) {
        // Sensor: For each direction the Neighbour-Part (Part-Type, Field(?)).
        final GridNode gridNode = lifePart.getGridNode();
        final Brain brain = lifePart.getBrain();
        for (final Cell.Dir dir : Cell.Dir.values()) {
            final GridNode neighbourGridNode = this.hexGridService.getNeighbourGridNode(gridNode, dir);
            final List<Part> neighbourPartList = neighbourGridNode.getPartList(this.hexGridService.getActCellArrPos());
            if (!neighbourPartList.isEmpty()) {
                final Part neighbourPart = neighbourPartList.get(0);
                brain.setNeigbourPartTypeInput(dir, neighbourPart.getPartType());
            } else {
                brain.setNeigbourPartTypeInput(dir, null);
            }
        }
    }
}
