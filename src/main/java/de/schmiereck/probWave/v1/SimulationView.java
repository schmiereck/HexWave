package de.schmiereck.probWave.v1;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Map;

public class SimulationView extends JPanel {

    private volatile SimulationStateDto currentStateDto = null; // Use volatile for visibility
    private final int hexSize = 20; // Pixel radius of hexagons
    private final int padding = 30; // Padding around the grid

    // Pre-calculate constants for geometry
    private static final double SQRT3 = Math.sqrt(3.0);

    // Keep a reference to the grid to calculate preferred size
    private transient HexGrid grid; // Transient because JPanel is serializable

    public SimulationView() {
        setBackground(Color.DARK_GRAY);
    }

    // Method called by the view update thread
    public void updateState(SimulationStateDto newState) {
        this.currentStateDto = newState;
        // Request a repaint on the Event Dispatch Thread
        repaint();
    }

    // Store grid reference to calculate preferred size correctly
    public void setGridReference(HexGrid grid) {
        this.grid = grid;
        // Recalculate preferred size if the grid is set after initialization
        revalidate(); // Tells the layout manager to reconsider this component
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        SimulationStateDto dto = this.currentStateDto; // Local copy for thread safety
        if (dto == null || dto.nodeStates.isEmpty()) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Waiting for simulation data...", 50, 50);
            return;
        }

        // Calculate offset to center the grid roughly (optional, but nice)
        Dimension currentSize = getSize();
        Dimension prefSize = getPreferredSize(); // Use the calculated preferred size
        int offsetX = padding + Math.max(0, (currentSize.width - prefSize.width) / 2);
        int offsetY = padding + Math.max(0, (currentSize.height - prefSize.height) / 2);


        // Draw all nodes
        for (NodeStateDto nodeState : dto.nodeStates.values()) {
            // Pass the calculated offset to the drawing method
            drawHexagon(g2d, nodeState, dto.minValue, dto.maxValue, offsetX, offsetY);
        }
    }

    // Added offsetX, offsetY parameters
    private void drawHexagon(Graphics2D g2d, NodeStateDto nodeState, int minVal, int maxVal, int offsetX, int offsetY) {
        // Calculate pixel center relative to (0,0), then add offset
        Point pixelCenter = hexToPixel(nodeState.getCoords(), hexSize);
        pixelCenter.translate(offsetX, offsetY); // Apply offset for centering/padding

        Path2D hexagon = createHexagonPath(pixelCenter, hexSize);

        // --- Fill color based on stateValue ---
        int range = Math.max(1, maxVal - minVal); // Avoid division by zero
        float normalizedValue = (float) (nodeState.stateValue - minVal) / range;
        // Example Heatmap (Blue -> Green -> Red)
        Color fillColor = Color.getHSBColor( (1.0f - normalizedValue) * 2.0f / 3.0f, 1.0f, 0.9f);


        g2d.setColor(fillColor);
        g2d.fill(hexagon);

        // --- Outline ---
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(hexagon);

        // --- Draw Probability Angle ---
        drawAngleLine(g2d, pixelCenter, nodeState.probabilityAngle, hexSize * 0.8, Color.CYAN);

        // --- Draw Velocity Angle ---
        drawAngleLine(g2d, pixelCenter, nodeState.velocityAngle, hexSize * 0.6, Color.MAGENTA);
    }

    private void drawAngleLine(Graphics2D g2d, Point center, double angleDegrees, double length, Color color) {
        double angleRad = Math.toRadians(angleDegrees);
        // Use double for intermediate calculation before rounding
        double endXExact = center.x + Math.cos(angleRad) * length;
        double endYExact = center.y + Math.sin(angleRad) * length; // Y is down in graphics

        g2d.setColor(color);
        // Draw line using integer coords
        g2d.drawLine(center.x, center.y, (int) Math.round(endXExact), (int) Math.round(endYExact));
    }


    // --- Hexagon Geometry --- (Pointy Top orientation)

    /**
     * Converts axial hex coordinates (q, r) to pixel coordinates (center of hex).
     * Uses rounding for potentially better accuracy than truncation via casting.
     * Assumes pointy-top orientation.
     *
     * @param hexCoords Axial coordinates (q, r) stored in a Point.
     * @param size      Radius of the hexagon in pixels.
     * @return Pixel coordinates (x, y) of the hexagon's center relative to a (0,0) origin.
     */
    private Point hexToPixel(Point hexCoords, int size) {
        int q = hexCoords.x;
        int r = hexCoords.y;
        // pointy top formula
        double x = size * (1.5 * q);
        double y = size * (SQRT3 / 2.0 * q + SQRT3 * r);
        // Round to nearest integer instead of truncating
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    private Path2D createHexagonPath(Point center, int size) {
        Path2D path = new Path2D.Double();
        for (int i = 0; i < 6; i++) {
            Point corner = hexCorner(center, size, i);
            if (i == 0) {
                path.moveTo(corner.x, corner.y);
            } else {
                path.lineTo(corner.x, corner.y);
            }
        }
        path.closePath();
        return path;
    }

    /**
     * Calculates the pixel coordinates of a hexagon's corner.
     * Uses rounding for potentially better accuracy.
     *
     * @param center Center pixel coordinates of the hexagon.
     * @param size   Radius of the hexagon.
     * @param i      Corner index (0-5).
     * @return Pixel coordinates of the corner.
     */
    private Point hexCorner(Point center, int size, int i) {
        // Angle in radians, starting from positive X axis (0 degrees)
        // Pointy top means corners are at 30, 90, 150, 210, 270, 330 degrees
        double angleDeg = 60 * i + 30;
        double angleRad = Math.toRadians(angleDeg);
        // Use double for intermediate calculation, then round
        double cornerXExact = center.x + size * Math.cos(angleRad);
        double cornerYExact = center.y + size * Math.sin(angleRad); // Y is down in graphics
        return new Point((int) Math.round(cornerXExact), (int) Math.round(cornerYExact));
    }

    // Override preferred size for layout based on grid dimensions
    @Override
    public Dimension getPreferredSize() {
        if (grid == null) {
            // Default size if grid is not yet known
            return new Dimension(600, 400);
        }

        // Calculate approximate bounds based on the MIN/MAX q and r values used
        // This requires knowing how the grid was populated. A simpler approach
        // uses width/height, but might overestimate if the grid isn't rectangular.

        // Approximate using width/height from HexGrid (might be larger than needed)
        // Width: Roughly 1.5 * size per q step. Max q range is roughly grid.getWidth().
        // Height: Roughly sqrt(3) * size per r step. Max r range is grid.getHeight().
        // Add contribution from q to height as well.

        // Let's use a slightly more direct calculation based on hex geometry:
        int effectiveWidth = grid.getWidth(); // Number of columns used in initialization
        int effectiveHeight = grid.getHeight(); // Number of rows

        // Width estimation: (number of columns - 1) * horizontal_spacing + one_hex_width
        double hexWidth = 2.0 * hexSize;
        double horizSpacing = 1.5 * hexSize;
        int approxWidth = (int) Math.round((effectiveWidth > 0 ? (effectiveWidth - 1) * horizSpacing + hexWidth : hexWidth));

        // Height estimation: (number of rows - 1) * vertical_spacing + one_hex_height
        double hexHeight = SQRT3 * hexSize;
        double vertSpacing = hexHeight; // For pointy top, row spacing is the hex height
        int approxHeight = (int) Math.round((effectiveHeight > 0 ? (effectiveHeight - 1) * vertSpacing + hexHeight: hexHeight));


        // Add padding to the calculated dimensions
        return new Dimension(approxWidth + 2 * padding, approxHeight + 2 * padding);
    }
}
