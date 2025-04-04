package de.schmiereck.probWave.v2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class TriangularSimulationView extends JPanel { // Renamed

    final SimulationService simulationService;

    private volatile SimulationStateDto currentStateDto = null;
    private final int vertexSpacingSize = 20; // Approx distance between vertices (like hexSize before)
    private final int padding = 30;
    private static final double LENGTH = 3.0D;
    private static final double LENGTH_2 = LENGTH / 2.0D;
    private static final double LENGTH_4 = LENGTH / 4.0D;
    private static final double SQRT = Math.sqrt(LENGTH);
    private static final double SQRT_2 = SQRT / 2.0D;
    //private transient TriangularGrid grid; // Use TriangularGrid

    public TriangularSimulationView(final SimulationService simulationService) {
        this.simulationService = simulationService;
        this.setBackground(Color.DARK_GRAY);
    }

    public void updateState(final SimulationStateDto newState) {
        this.currentStateDto = newState;
        this.repaint();
    }

    //public void setGridReference(final TriangularGrid grid) {
    //    this.grid = grid;
    //    this.revalidate();
    //}

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final SimulationStateDto dto = this.currentStateDto;
        if (dto == null) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Waiting for simulation data...", 50, 50);
            return;
        }

        final int offsetX = this.padding;
        final int offsetY = this.padding;

        final VertexNodeStateDto[][] nodeArr = dto.nodeStateArr;

        // --- Draw Triangles ---
        // Iterate through each vertex as a potential top/left point of triangles
        for (int xPos = 0; xPos < dto.width - 1; xPos++) {
            for (int yPos = 0; yPos < dto.height - 1; yPos++) {
                final VertexNodeStateDto nodeA = nodeArr[xPos][yPos];

                final Point coordA = nodeA.getCoords();
                // Find potential neighbors for triangles (B and C for Up, B and D for Down)

                if (yPos % 2 == 0) { // Even row
                    final VertexNodeStateDto nodeB = nodeArr[coordA.x + 1][coordA.y]; // Neighbor right
                    final VertexNodeStateDto nodeC = nodeArr[coordA.x][coordA.y + 1];
                    // Draw UPWARD triangle (A, B, C) if B and C exist
                    drawTriangle(g2d, nodeA, nodeB, nodeC, dto.minValue, dto.maxValue, offsetX, offsetY);
                    if (xPos < dto.width - 1) {
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
                    if (xPos < dto.width - 1) {
                        final VertexNodeStateDto nodeB = nodeArr[coordA.x + 1][coordA.y]; // Neighbor right (for Down triangle)
                        drawTriangle(g2d, nodeA, nodeB, nodeD, dto.minValue, dto.maxValue, offsetX, offsetY);
                    }
                }
            }
        }

        // --- Draw Angle lines on top of triangles ---
        g2d.setStroke(new BasicStroke(1.0f)); // Thinner lines maybe?
        for (int xPos = 0; xPos < dto.width; xPos++) {
            for (int yPos = 0; yPos < dto.height; yPos++) {
                final VertexNodeStateDto nodeState = nodeArr[xPos][yPos];
                final Point pixelCenter = vertexCoordsToPixel(nodeState.getCoords(), this.vertexSpacingSize);
                pixelCenter.translate(offsetX, offsetY);
                // Draw lines shorter, maybe half the spacing size
                final double lineLength = this.vertexSpacingSize * 0.4;
                drawAngleLine(g2d, pixelCenter, nodeState.probabilityAngle, lineLength, Color.CYAN);
                drawAngleLine(g2d, pixelCenter, nodeState.velocityAngle, lineLength * 0.75, Color.MAGENTA);
            }
        }
    }

    // Helper to draw a single triangle based on 3 vertices
    private void drawTriangle(final Graphics2D g2d,
                              final VertexNodeStateDto v1, final VertexNodeStateDto v2, final VertexNodeStateDto v3,
                              final int minVal, final int maxVal, final int offsetX, final int offsetY)
    {
        final Point p1 = vertexCoordsToPixel(v1.getCoords(), this.vertexSpacingSize);
        final Point p2 = vertexCoordsToPixel(v2.getCoords(), this.vertexSpacingSize);
        final Point p3 = vertexCoordsToPixel(v3.getCoords(), this.vertexSpacingSize);

        p1.translate(offsetX, offsetY);
        p2.translate(offsetX, offsetY);
        p3.translate(offsetX, offsetY);

        final Path2D trianglePath = new Path2D.Double();
        trianglePath.moveTo(p1.x, p1.y);
        trianglePath.lineTo(p2.x, p2.y);
        trianglePath.lineTo(p3.x, p3.y);
        trianglePath.closePath();

        // --- Color based on average state value of vertices ---
        final double avgValue = (v1.stateValue + v2.stateValue + v3.stateValue) / 3.0;
        final int range = Math.max(1, maxVal - minVal);
        // Clamp normalized value just in case
        final float normalizedValue = Math.max(0.0f, Math.min(1.0f, (float) (avgValue - minVal) / range));

        final Color fillColor = Color.getHSBColor((1.0f - normalizedValue) * 2.0f / 3.0f, 1.0f, 0.9f);

        g2d.setColor(fillColor);
        g2d.fill(trianglePath);

        // --- Outline ---
        g2d.setColor(Color.DARK_GRAY.brighter()); // Slightly brighter outline
        g2d.draw(trianglePath);
    }


    // Renamed from hexToPixel - formula remains the same for axial coords -> pixels
    private Point vertexCoordsToPixel(final Point hexCoords, final int size) {
        final int q = hexCoords.x;
        final int r = hexCoords.y;
        final double x;
        final double y;
        if (r % 2 == 0) { // Even row
            x = size * (LENGTH_2 * q);
            y = size * (SQRT_2 * r);
        } else { // Odd row
            x = size * (LENGTH_2 * q + LENGTH_4);
            y = size * (SQRT_2 * r);
        }
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    // Draw angle line from vertex center
    private void drawAngleLine(final Graphics2D g2d, final Point center, final double angleDegrees, final double length, final Color color) {
        final double angleRad = Math.toRadians(angleDegrees);
        final double endXExact = center.x + Math.cos(angleRad) * length;
        final double endYExact = center.y + Math.sin(angleRad) * length;
        g2d.setColor(color);
        g2d.drawLine(center.x, center.y, (int) Math.round(endXExact), (int) Math.round(endYExact));
    }

    @Override
    public Dimension getPreferredSize() {
        final TriangularGridDto triangularGridDto = this.simulationService.retrieveTriangularGridDto();
        if (triangularGridDto == null) {
            return new Dimension(600, 400);
        }

        // Estimate size based on vertex grid extent - similar logic to before
        final int effectiveWidth = triangularGridDto.getWidth();
        final int effectiveHeight = triangularGridDto.getHeight();

        // Use vertexSpacingSize instead of hexSize
        final double nodeWidth = 2.0 * this.vertexSpacingSize; // Not really used directly here
        final double horizSpacing = 1.5 * this.vertexSpacingSize;
        final double nodeHeight = SQRT * this.vertexSpacingSize;
        final double vertSpacing = nodeHeight;

        // Adjusting calculation slightly - might need refinement based on actual vertex range
        // Find actual min/max q and r from the grid for more accuracy
        int minQ = 0, maxQ = 0, minR = 0, maxR = 0;
        boolean first = true;
        if (this.currentStateDto != null) {
            for (int xPos = 0; xPos < this.currentStateDto.width; xPos++) {
                for (int yPos = 0; yPos < this.currentStateDto.height; yPos++) {
                    final VertexNodeStateDto nodeState = this.currentStateDto.nodeStateArr[xPos][yPos];
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
            maxQ = effectiveWidth - 1;
            maxR = effectiveHeight - 1;
        }

        final Point Pmin = vertexCoordsToPixel(new Point(minQ, minR), this.vertexSpacingSize);
        final Point PmaxQ = vertexCoordsToPixel(new Point(maxQ, minR), this.vertexSpacingSize); // Max Q influence
        final Point PmaxR = vertexCoordsToPixel(new Point(minQ, maxR), this.vertexSpacingSize); // Max R influence
        final Point PmaxQR = vertexCoordsToPixel(new Point(maxQ, maxR), this.vertexSpacingSize); // Corner

        // Rough estimation of pixel bounds
        final int minX = Math.min(Pmin.x, PmaxR.x);
        final int maxX = Math.max(PmaxQ.x, PmaxQR.x);
        final int minY = Pmin.y; // Assuming pointy top, min r gives min y
        final int maxY = Math.max(PmaxR.y, PmaxQR.y);

        // Add a buffer for triangle points extending beyond vertices and padding
        final int approxWidth = maxX - minX + this.vertexSpacingSize * 2; // Add buffer
        final int approxHeight = maxY - minY + this.vertexSpacingSize * 2; // Add buffer

        return new Dimension(approxWidth + 2 * this.padding, approxHeight + 2 * this.padding);
    }
}
