package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.math.HexParticle;
import de.schmiereck.hexWave.math.HexVector;
import de.schmiereck.hexWave.math.MoveHexVector;
import de.schmiereck.hexWave.service.brain.Brain;
import de.schmiereck.hexWave.service.hexGrid.GridNode;
import de.schmiereck.hexWave.service.hexGrid.Part;

public class LifePart {
    public final PartIdentity partIdentity;
    private final Brain brain;
    private final Part part;
    private GridNode gridNode;

    public LifePart(final PartIdentity partIdentity, final Brain brain, final GridNode gridNode, final Part part) {
        this.partIdentity = partIdentity;
        this.brain = brain;
        this.gridNode = gridNode;
        this.part = part;
    }

    public Brain getBrain() {
        return this.brain;
    }

    public Part getPart() {
        return this.part;
    }

    public GridNode getGridNode() {
        return this.gridNode;
    }

    public void setGridNode(GridNode gridNode) {
        this.gridNode = gridNode;
    }
}