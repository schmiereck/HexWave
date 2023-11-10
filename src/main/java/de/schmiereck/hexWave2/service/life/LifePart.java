package de.schmiereck.hexWave2.service.life;

import de.schmiereck.hexWave2.service.hexGrid.GridNode;
import de.schmiereck.hexWave2.service.hexGrid.Part;

import java.io.Serializable;

public class LifePart implements Serializable {
    public final PartIdentity partIdentity;
    private final Part part;
    private transient GridNode gridNode;
    private final int startStepCounter;

    public LifePart(final PartIdentity partIdentity, final GridNode gridNode, final Part part, final int startStepCounter) {
        this.partIdentity = partIdentity;
        this.gridNode = gridNode;
        this.part = part;
        this.startStepCounter = startStepCounter;
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

    public int getStartStepCounter() {
        return this.startStepCounter;
    }
}