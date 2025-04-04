package de.schmiereck.probWave.v2;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TriangularGrid { // Renamed from HexGrid

    private final int width;
    private final int height;
    private final VertexNode[][] nodeArr;
    private final int minValue;
    private final int maxValue;

    public TriangularGrid(final int width, final int height, final int minValue, final int maxValue) {
        this.width = width;
        this.height = height;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.nodeArr = new VertexNode[this.width][this.height];

        initializeGrid(); // Creates the vertices
    }

    private void initializeGrid() {
        final Random rand = new Random();

        for (int xPos = 0; xPos < this.width; xPos++) {
            for (int yPos = 0; yPos < this.height; yPos++) {
                final int q = xPos;
                final int r = yPos;
                final int initialValue = minValue + rand.nextInt(maxValue - minValue + 1);
                final double initialProbAngle = rand.nextDouble() * 360.0;
                final double initialVelAngle = rand.nextDouble() * 360.0;
                this.nodeArr[xPos][yPos] = new VertexNode(q, r, initialValue, initialProbAngle, initialVelAngle);
            }
        }
        System.out.println("Initialized triangular grid with " + (this.width * this.height) + " vertices.");
    }

    public VertexNode getNode(final int q, final int r) {
        return this.nodeArr[q][r];
    }

    // Neighbors are still defined by the same axial coordinate offsets
    public List<VertexNode> getNeighbors(final VertexNode node) {
        final List<VertexNode> neighbors = new ArrayList<>(6);
        // TODO : Use the node's coordinates to find neighbors
        //Point centerCoords = node.getCoords();
        //for (Point offset : NEIGHBOR_OFFSETS) {
        //    Point neighborCoords = new Point(centerCoords.x + offset.x, centerCoords.y + offset.y);
        //    VertexNode neighbor = nodes.get(neighborCoords);
        //    if (neighbor != null) {
        //        neighbors.add(neighbor);
        //    }
        //}
        return neighbors;
    }

    // Getters for grid properties remain the same
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getMinValue() { return this.minValue; }
    public int getMaxValue() { return this.maxValue; }
}
