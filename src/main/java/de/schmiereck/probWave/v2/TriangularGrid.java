package de.schmiereck.probWave.v2;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TriangularGrid { // Renamed from HexGrid

    private final int width;
    private final int height;
    // Map holds VertexNodes now
    //private final Map<Point, VertexNode> nodes;
    private final VertexNode[][] nodeArr;
    private final int minValue;
    private final int maxValue;

    // Neighbor offsets remain the same for axial coordinates
    private static final Point[] NEIGHBOR_OFFSETS = {
            new Point(1, 0), new Point(1, -1), new Point(0, -1),
            new Point(-1, 0), new Point(-1, 1), new Point(0, 1)
    };

    public TriangularGrid(int width, int height, int minValue, int maxValue) {
        this.width = width;
        this.height = height;
        this.minValue = minValue;
        this.maxValue = maxValue;
        //this.nodes = new ConcurrentHashMap<>();
        this.nodeArr = new VertexNode[this.width][this.height];

        initializeGrid(); // Creates the vertices
    }

    private void initializeGrid() {
        Random rand = new Random();
        //nodes.clear();
        // This loop creates the vertex coordinates using the original parallelogram shape
        //for (int r = 0; r < height; r++) {
        //    for (int q = -r / 2; q < width - r / 2; q++) {
        //        int initialValue = minValue + rand.nextInt(maxValue - minValue + 1);
        //        double initialProbAngle = rand.nextDouble() * 360.0;
        //        double initialVelAngle = rand.nextDouble() * 360.0;
        //        Point coords = new Point(q, r);
        //        nodes.put(coords, new VertexNode(q, r, initialValue, initialProbAngle, initialVelAngle));
        //    }
        //}
        for (int xPos = 0; xPos < this.width; xPos++) {
            for (int yPos = 0; yPos < this.height; yPos++) {
                int q = xPos;
                int r = yPos;
                int initialValue = minValue + rand.nextInt(maxValue - minValue + 1);
                double initialProbAngle = rand.nextDouble() * 360.0;
                double initialVelAngle = rand.nextDouble() * 360.0;
                this.nodeArr[xPos][yPos] = new VertexNode(q, r, initialValue, initialProbAngle, initialVelAngle);
            }
        }
        //System.out.println("Initialized triangular grid with " + nodes.size() + " vertices.");
        System.out.println("Initialized triangular grid with " + (this.width * this.height) + " vertices.");
    }

    public VertexNode getNode(int q, int r) {
        //return nodes.get(new Point(q, r));
        return this.nodeArr[q][r];
    }

    //public VertexNode getNode(Point coords) {
    //    return nodes.get(coords);
    //}

    //public Collection<VertexNode> getAllNodes() {
    //    return nodes.values();
    //}

    // Neighbors are still defined by the same axial coordinate offsets
    public List<VertexNode> getNeighbors(VertexNode node) {
        List<VertexNode> neighbors = new ArrayList<>(6);
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
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getMinValue() { return minValue; }
    public int getMaxValue() { return maxValue; }
}
