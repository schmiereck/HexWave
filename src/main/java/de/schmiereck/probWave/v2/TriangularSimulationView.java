package de.schmiereck.probWave.v2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Map;

public class TriangularSimulationView extends JPanel { // Renamed

    private volatile SimulationStateDto currentStateDto = null;
    private final int vertexSpacingSize = 20; // Approx distance between vertices (like hexSize before)
    private final int padding = 30;
    private static final double SQRT3 = Math.sqrt(3.0);
    private transient TriangularGrid grid; // Use TriangularGrid

    public TriangularSimulationView() {
        setBackground(Color.DARK_GRAY);
    }

    public void updateState(SimulationStateDto newState) {
        this.currentStateDto = newState;
        repaint();
    }

    public void setGridReference(TriangularGrid grid) { // Use TriangularGrid
        this.grid = grid;
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        SimulationStateDto dto = this.currentStateDto;
        //if (dto == null || dto.nodeStates.isEmpty()) {
        if (dto == null) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Waiting for simulation data...", 50, 50);
            return;
        }

        int offsetX = padding;
        int offsetY = padding;

        //Map<Point, VertexNodeStateDto> nodeMap = dto.nodeStates;
        final VertexNodeStateDto[][] nodeArr = dto.nodeStateArr;

        // --- Draw Triangles ---
        // Iterate through each vertex as a potential top/left point of triangles
        //for (VertexNodeStateDto nodeA : nodeMap.values()) {
        //    Point coordA = nodeA.getCoords();
        //
        //    // Find potential neighbors for triangles (B and C for Up, B and D for Down)
        //    Point coordB = new Point(coordA.x + 1, coordA.y);     // Neighbor right
        //    Point coordC = new Point(coordA.x, coordA.y + 1);     // Neighbor down-left (for Up triangle)
        //    Point coordD = new Point(coordA.x + 1, coordA.y - 1); // Neighbor up-right (for Down triangle)
        //
        //    VertexNodeStateDto nodeB = nodeMap.get(coordB);
        //    VertexNodeStateDto nodeC = nodeMap.get(coordC);
        //    VertexNodeStateDto nodeD = nodeMap.get(coordD);
        //
        //    // Draw UPWARD triangle (A, B, C) if B and C exist
        //    if (nodeB != null && nodeC != null) {
        //        drawTriangle(g2d, nodeA, nodeB, nodeC, dto.minValue, dto.maxValue, offsetX, offsetY);
        //    }
        //
        //    // Draw DOWNWARD triangle (A, D, B) if D and B exist
        //    // Note the order A, D, B to maintain winding for potential backface culling (not used here)
        //    if (nodeD != null && nodeB != null) {
        //        // Reusing drawTriangle, order matters for visual consistency if needed
        //        drawTriangle(g2d, nodeA, nodeD, nodeB, dto.minValue, dto.maxValue, offsetX, offsetY);
        //    }
        //}
        for (int xPos = 0; xPos < this.grid.getWidth() - 1; xPos++) {
            for (int yPos = 0; yPos < this.grid.getHeight() - 1; yPos++) {
                final VertexNodeStateDto nodeA = nodeArr[xPos][yPos];

                final Point coordA = nodeA.getCoords();
                // Find potential neighbors for triangles (B and C for Up, B and D for Down)

                if (yPos % 2 == 0) { // Even row
                    final VertexNodeStateDto nodeB = nodeArr[coordA.x + 1][coordA.y]; // Neighbor right
                    final VertexNodeStateDto nodeC = nodeArr[coordA.x][coordA.y + 1];
                    // Draw UPWARD triangle (A, B, C) if B and C exist
                    drawTriangle(g2d, nodeA, nodeB, nodeC, dto.minValue, dto.maxValue, offsetX, offsetY);
                    if (xPos < this.grid.getWidth() - 1) {
                        final VertexNodeStateDto nodeD = nodeArr[coordA.x + 1][coordA.y + 1]; // Neighbor up-right (for Down triangle)
                        // Draw DOWNWARD triangle (A, D, B) if D and B exist
                        // Note the order A, D, B to maintain winding for potential backface culling (not used here)
                        drawTriangle(g2d, nodeB, nodeD, nodeC, dto.minValue, dto.maxValue, offsetX, offsetY);
                    }
                } else { // Odd row
                    final VertexNodeStateDto nodeC = nodeArr[coordA.x][coordA.y + 1];    // Neighbor down-left (for Up triangle)
                    final VertexNodeStateDto nodeD = nodeArr[coordA.x + 1][coordA.y + 1];// Neighbor up-right (for Down triangle)
                    // Draw DOWNWARD triangle (A, D, B) if D and B exist
                    // Note the order A, D, B to maintain winding for potential backface culling (not used here)
                    // Reusing drawTriangle, order matters for visual consistency if needed
                    drawTriangle(g2d, nodeA, nodeC, nodeD, dto.minValue, dto.maxValue, offsetX, offsetY);
                    if (xPos < this.grid.getWidth() - 1) {
                        final VertexNodeStateDto nodeB = nodeArr[coordA.x + 1][coordA.y]; // Neighbor right (for Down triangle)
                        drawTriangle(g2d, nodeA, nodeB, nodeD, dto.minValue, dto.maxValue, offsetX, offsetY);
                    }
                }
            }
        }

        // --- Draw Angle lines on top of triangles ---
        g2d.setStroke(new BasicStroke(1.0f)); // Thinner lines maybe?
        for (int xPos = 0; xPos < this.grid.getWidth(); xPos++) {
            for (int yPos = 0; yPos < this.grid.getHeight(); yPos++) {
                final VertexNodeStateDto nodeState = nodeArr[xPos][yPos];
                Point pixelCenter = vertexCoordsToPixel(nodeState.getCoords(), vertexSpacingSize);
                pixelCenter.translate(offsetX, offsetY);
                // Draw lines shorter, maybe half the spacing size
                double lineLength = vertexSpacingSize * 0.4;
                drawAngleLine(g2d, pixelCenter, nodeState.probabilityAngle, lineLength, Color.CYAN);
                drawAngleLine(g2d, pixelCenter, nodeState.velocityAngle, lineLength * 0.75, Color.MAGENTA);
            }
        }
    }

    // Helper to draw a single triangle based on 3 vertices
    private void drawTriangle(Graphics2D g2d,
                              VertexNodeStateDto v1, VertexNodeStateDto v2, VertexNodeStateDto v3,
                              int minVal, int maxVal, int offsetX, int offsetY)
    {
        Point p1 = vertexCoordsToPixel(v1.getCoords(), vertexSpacingSize);
        Point p2 = vertexCoordsToPixel(v2.getCoords(), vertexSpacingSize);
        Point p3 = vertexCoordsToPixel(v3.getCoords(), vertexSpacingSize);

        p1.translate(offsetX, offsetY);
        p2.translate(offsetX, offsetY);
        p3.translate(offsetX, offsetY);

        Path2D trianglePath = new Path2D.Double();
        trianglePath.moveTo(p1.x, p1.y);
        trianglePath.lineTo(p2.x, p2.y);
        trianglePath.lineTo(p3.x, p3.y);
        trianglePath.closePath();

        // --- Color based on average state value of vertices ---
        double avgValue = (v1.stateValue + v2.stateValue + v3.stateValue) / 3.0;
        int range = Math.max(1, maxVal - minVal);
        float normalizedValue = (float) (avgValue - minVal) / range;
        // Clamp normalized value just in case
        normalizedValue = Math.max(0.0f, Math.min(1.0f, normalizedValue));

        Color fillColor = Color.getHSBColor((1.0f - normalizedValue) * 2.0f / 3.0f, 1.0f, 0.9f);

        g2d.setColor(fillColor);
        g2d.fill(trianglePath);

        // --- Outline ---
        g2d.setColor(Color.DARK_GRAY.brighter()); // Slightly brighter outline
        g2d.draw(trianglePath);
    }


    // Renamed from hexToPixel - formula remains the same for axial coords -> pixels
    private Point vertexCoordsToPixel(Point hexCoords, int size) {
        int q = hexCoords.x;
        int r = hexCoords.y;
        final double x;
        final double y;
        if (r % 2 == 0) { // Even row
            x = size * (1.5D * q);
            y = size * (SQRT3 / 2.0D * r);
        } else { // Odd row
            x = size * (1.5D * q + 0.75D);
            y = size * (SQRT3 / 2.0D * r);
        }
        //y = size * (SQRT3 * r);
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    // Draw angle line from vertex center
    private void drawAngleLine(Graphics2D g2d, Point center, double angleDegrees, double length, Color color) {
        double angleRad = Math.toRadians(angleDegrees);
        double endXExact = center.x + Math.cos(angleRad) * length;
        double endYExact = center.y + Math.sin(angleRad) * length;
        g2d.setColor(color);
        g2d.drawLine(center.x, center.y, (int) Math.round(endXExact), (int) Math.round(endYExact));
    }

    @Override
    public Dimension getPreferredSize() {
        if (grid == null) return new Dimension(600, 400);

        // Estimate size based on vertex grid extent - similar logic to before
        int effectiveWidth = grid.getWidth();
        int effectiveHeight = grid.getHeight();

        // Use vertexSpacingSize instead of hexSize
        double nodeWidth = 2.0 * vertexSpacingSize; // Not really used directly here
        double horizSpacing = 1.5 * vertexSpacingSize;
        double nodeHeight = SQRT3 * vertexSpacingSize;
        double vertSpacing = nodeHeight;

        // Adjusting calculation slightly - might need refinement based on actual vertex range
        // Find actual min/max q and r from the grid for more accuracy
        int minQ = 0, maxQ = 0, minR = 0, maxR = 0;
        boolean first = true;
        //if (currentStateDto != null && !currentStateDto.nodeStates.isEmpty()) {
        if (currentStateDto != null) {
            //for (Point p : currentStateDto.nodeStates.keySet()) {
            //    if (first) {
            //        minQ = maxQ = p.x;
            //        minR = maxR = p.y;
            //        first = false;
            //    } else {
            //        minQ = Math.min(minQ, p.x);
            //        maxQ = Math.max(maxQ, p.x);
            //        minR = Math.min(minR, p.y);
            //        maxR = Math.max(maxR, p.y);
            //    }
            //}
            for (int xPos = 0; xPos < this.grid.getWidth(); xPos++) {
                for (int yPos = 0; yPos < this.grid.getHeight(); yPos++) {
                    final VertexNodeStateDto nodeState = currentStateDto.nodeStateArr[xPos][yPos];
                    final Point p = nodeState.getCoords();
                    if (first) {
                        minQ = maxQ = p.x;
                        minR = maxR = p.y;
                        first = false;
                    } else {
                        minQ = Math.min(minQ, p.x);
                        maxQ = Math.max(maxQ, p.x);
                        minR = Math.min(minR, p.y);
                        maxR = Math.max(maxR, p.y);
                    }
                }
            }
        } else { // Fallback if DTO not ready
            maxQ = effectiveWidth -1;
            maxR = effectiveHeight -1;
        }


        Point Pmin = vertexCoordsToPixel(new Point(minQ, minR), vertexSpacingSize);
        Point PmaxQ = vertexCoordsToPixel(new Point(maxQ, minR), vertexSpacingSize); // Max Q influence
        Point PmaxR = vertexCoordsToPixel(new Point(minQ, maxR), vertexSpacingSize); // Max R influence
        Point PmaxQR = vertexCoordsToPixel(new Point(maxQ, maxR), vertexSpacingSize); // Corner

        // Rough estimation of pixel bounds
        int minX = Math.min(Pmin.x, PmaxR.x);
        int maxX = Math.max(PmaxQ.x, PmaxQR.x);
        int minY = Pmin.y; // Assuming pointy top, min r gives min y
        int maxY = Math.max(PmaxR.y, PmaxQR.y);


        // Add a buffer for triangle points extending beyond vertices and padding
        int approxWidth = maxX - minX + vertexSpacingSize*2; // Add buffer
        int approxHeight = maxY - minY + vertexSpacingSize*2; // Add buffer

        return new Dimension(approxWidth + 2 * padding, approxHeight + 2 * padding);
    }
}
