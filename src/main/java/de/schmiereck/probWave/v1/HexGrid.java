package de.schmiereck.probWave.v1;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap; // Using thread-safe map

public class HexGrid {

    private final int width; // Number of columns (can be adapted based on hex layout)
    private final int height; // Number of rows
    private final Map<Point, HexNode> nodes; // Using Point for axial coords (q, r)
    private final int minValue;
    private final int maxValue;

    // Neighbor offsets in axial coordinates
    private static final Point[] NEIGHBOR_OFFSETS = {
            new Point(1, 0), new Point(1, -1), new Point(0, -1),
            new Point(-1, 0), new Point(-1, 1), new Point(0, 1)
    };

    public HexGrid(int width, int height, int minValue, int maxValue) {
        this.width = width;
        this.height = height;
        this.minValue = minValue;
        this.maxValue = maxValue;
        // Using ConcurrentHashMap is safer if grid structure could change,
        // but for fixed grid, regular HashMap accessed carefully is also ok.
        this.nodes = new ConcurrentHashMap<>();
        initializeGrid();
    }

    private void initializeGrid() {
        Random rand = new Random();
        // Initialize nodes in a roughly hexagonal area (adjust loop bounds as needed)
        for (int r = 0; r < height; r++) {
            for (int q = -r / 2; q < width - r / 2; q++) {
                int initialValue = minValue + rand.nextInt(maxValue - minValue + 1);
                double initialProbAngle = rand.nextDouble() * 360.0;
                double initialVelAngle = rand.nextDouble() * 360.0;
                Point coords = new Point(q, r);
                nodes.put(coords, new HexNode(q, r, initialValue, initialProbAngle, initialVelAngle));
            }
        }
        System.out.println("Initialized grid with " + nodes.size() + " nodes.");
    }

    // --- Temporäre einfache rechteckige Initialisierung ---
    private void initializeGrid_dbg() {
        Random rand = new Random();
        // Verwende die thread-sichere Map weiterhin
        nodes.clear(); // Stelle sicher, dass alte Nodes entfernt werden, falls die Methode erneut aufgerufen wird

        System.out.println("DEBUG: Initializing grid with simple rectangular block (q=0..width-1, r=0..height-1)");

        // --- Temporäre einfache rechteckige Initialisierung ---
        for (int r = 0; r < height; r++) {
            for (int q = 0; q < width; q++) { // q läuft jetzt einfach von 0 bis width-1
                int initialValue = minValue + rand.nextInt(maxValue - minValue + 1);
                double initialProbAngle = rand.nextDouble() * 360.0;
                double initialVelAngle = rand.nextDouble() * 360.0;
                Point coords = new Point(q, r);
                // Stelle sicher, dass die Map den neuen Node aufnimmt
                nodes.put(coords, new HexNode(q, r, initialValue, initialProbAngle, initialVelAngle));
            }
        }
        // --- Ende der temporären Initialisierung ---

        System.out.println("Initialized grid with " + nodes.size() + " nodes.");
        // Optional: Min/Max q/r ausgeben, um den Bereich zu prüfen
        if (!nodes.isEmpty()) {
            int minQ = nodes.keySet().stream().mapToInt(p -> p.x).min().orElse(0);
            int maxQ = nodes.keySet().stream().mapToInt(p -> p.x).max().orElse(0);
            int minR = nodes.keySet().stream().mapToInt(p -> p.y).min().orElse(0);
            int maxR = nodes.keySet().stream().mapToInt(p -> p.y).max().orElse(0);
            System.out.println("Grid coordinate ranges: q=[" + minQ + ".." + maxQ + "], r=[" + minR + ".." + maxR + "]");
        }
    }

    public HexNode getNode(int q, int r) {
        return nodes.get(new Point(q, r));
    }

    public HexNode getNode(Point coords) {
        return nodes.get(coords);
    }

    public Collection<HexNode> getAllNodes() {
        return nodes.values();
    }

    public List<HexNode> getNeighbors(HexNode node) {
        List<HexNode> neighbors = new ArrayList<>(6);
        Point centerCoords = node.getCoords();
        for (Point offset : NEIGHBOR_OFFSETS) {
            Point neighborCoords = new Point(centerCoords.x + offset.x, centerCoords.y + offset.y);
            HexNode neighbor = nodes.get(neighborCoords);
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
            // Else: Neighbor is outside the grid boundary
        }
        return neighbors;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getMinValue() { return minValue; }
    public int getMaxValue() { return maxValue; }
}
