package de.schmiereck.probWave.v2;

import java.util.*;

public class TriangularGrid { // Renamed from HexGrid

    private final int width;
    private final int height;
    private static final int TIME_CND = 2;
    private int actTimePos = 0;
    private final VertexNode[][][] nodeArr;
    private final int minValue;
    private final int maxValue;

    public TriangularGrid(final int width, final int height, final int minValue, final int maxValue) {
        this.width = width;
        this.height = height;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.nodeArr = new VertexNode[TIME_CND][this.width][this.height];

        initializeGrid(); // Creates the vertices
    }

    private void initializeGrid() {
        final Random rand = new Random();

        for (int timePos = 0; timePos < TIME_CND; timePos++) {
            for (int xPos = 0; xPos < this.width; xPos++) {
                for (int yPos = 0; yPos < this.height; yPos++) {
                    final int q = xPos;
                    final int r = yPos;
                    final int initialValue = minValue + rand.nextInt(maxValue - minValue + 1);
                    final double initialProbAngle = rand.nextDouble() * 360.0;
                    final double initialVelAngle = rand.nextDouble() * 360.0;
                    this.nodeArr[timePos][xPos][yPos] = new VertexNode(q, r, initialValue, initialProbAngle, initialVelAngle);
                }
            }
        }
        System.out.println("Initialized triangular grid with " + (this.width * this.height) + " vertices.");
    }

    public VertexNode getActNode(final int q, final int r) {
        return this.nodeArr[this.actTimePos][q][r];
    }

    public VertexNode getNextNode(final int q, final int r) {
        return this.nodeArr[calcNextTimePos(this.actTimePos)][q][r];
    }

    public static int calcNextTimePos(int actTimePos) {
        return (actTimePos + 1) % TIME_CND;
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

    public void setActTimePos(int timePos) {
        this.actTimePos = timePos;
    }

    public int getActTimePos() {
        return this.actTimePos;
    }
}
