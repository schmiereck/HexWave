package de.schmiereck.probWave.v1;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D; // Importieren
import java.util.Map;

public class Simulation2View extends JPanel {

    private volatile SimulationStateDto currentStateDto = null;
    private final int hexSize = 20;
    private final int padding = 30;
    private static final double SQRT3 = Math.sqrt(3.0);
    private transient HexGrid grid;

    public Simulation2View() {
        setBackground(Color.DARK_GRAY);
    }

    public void updateState(SimulationStateDto newState) {
        this.currentStateDto = newState;
        repaint();
    }

    public void setGridReference(HexGrid grid) {
        this.grid = grid;
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        SimulationStateDto dto = this.currentStateDto;
        if (dto == null || dto.nodeStates.isEmpty()) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Waiting for simulation data...", 50, 50);
            return;
        }

        // --- Testweise feste Offsets verwenden (nur Padding) ---
        int offsetX = padding;
        int offsetY = padding;
        // --- Ende Test ---

    /* // --- Alte Zentrierungslogik (vorerst auskommentiert) ---
    Dimension currentSize = getSize();
    Dimension prefSize = getPreferredSize();
    int offsetX = padding + Math.max(0, (currentSize.width - prefSize.width) / 2);
    int offsetY = padding + Math.max(0, (currentSize.height - prefSize.height) / 2);
    */ // --- Ende alte Zentrierungslogik ---


        for (NodeStateDto nodeState : dto.nodeStates.values()) {
            drawHexagon(g2d, nodeState, dto.minValue, dto.maxValue, offsetX, offsetY);
        }
    }

    private void drawHexagon(Graphics2D g2d, NodeStateDto nodeState, int minVal, int maxVal, int offsetX, int offsetY) {
        // Berechne den Mittelpunkt relativ zu (0,0)
        Point pixelCenterInt = hexToPixel(nodeState.getCoords(), hexSize);
        // Erstelle einen Point2D.Double für präzisere Berechnungen, falls nötig,
        // aber da hexToPixel schon rundet, bleiben wir hier bei Point für den *Center*.
        // Addiere den Offset zum gerundeten Integer-Mittelpunkt.
        Point finalCenter = new Point(pixelCenterInt.x + offsetX, pixelCenterInt.y + offsetY);


        // Erstelle den Pfad mit höherer Präzision
        Path2D hexagon = createHexagonPath(finalCenter, hexSize); // Übergibt Point, aber hexCorner verwendet double

        int range = Math.max(1, maxVal - minVal);
        float normalizedValue = (float) (nodeState.stateValue - minVal) / range;
        Color fillColor = Color.getHSBColor((1.0f - normalizedValue) * 2.0f / 3.0f, 1.0f, 0.9f);

        g2d.setColor(fillColor);
        g2d.fill(hexagon); // fill nimmt Path2D entgegen

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(hexagon); // draw nimmt Path2D entgegen

        drawAngleLine(g2d, finalCenter, nodeState.probabilityAngle, hexSize * 0.8, Color.CYAN);
        drawAngleLine(g2d, finalCenter, nodeState.velocityAngle, hexSize * 0.6, Color.MAGENTA);
    }

    private void drawAngleLine(Graphics2D g2d, Point center, double angleDegrees, double length, Color color) {
        double angleRad = Math.toRadians(angleDegrees);
        double endXExact = center.x + Math.cos(angleRad) * length;
        double endYExact = center.y + Math.sin(angleRad) * length;

        g2d.setColor(color);
        g2d.drawLine(center.x, center.y, (int) Math.round(endXExact), (int) Math.round(endYExact));
    }

    // --- Hexagon Geometry ---

    private Point hexToPixel(Point hexCoords, int size) {
        int q = hexCoords.x;
        int r = hexCoords.y;
        // pointy top formula - Berechnungen als double durchführen
        double x = size * (1.5 * q);
        double y = size * (SQRT3 / 2.0 * q + SQRT3 * r);
        // Runden zum Schluss
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    // *** Änderung: hexCorner gibt Point2D.Double zurück ***
    private Point2D.Double hexCorner(Point center, int size, int i) {
        double angleDeg = 60 * i + 30;
        double angleRad = Math.toRadians(angleDeg);
        // Berechne Eckpunkte als double, basierend auf dem (möglicherweise gerundeten) center Point
        double cornerXExact = center.x + size * Math.cos(angleRad);
        double cornerYExact = center.y + size * Math.sin(angleRad);
        // Gib Point2D.Double zurück
        return new Point2D.Double(cornerXExact, cornerYExact);
    }

    // *** Änderung: createHexagonPath verwendet Point2D.Double von hexCorner ***
    private Path2D createHexagonPath(Point center, int size) {
        Path2D path = new Path2D.Double(); // Pfad für double Koordinaten
        for (int i = 0; i < 6; i++) {
            // Ruft die geänderte hexCorner Methode auf
            Point2D.Double corner = hexCorner(center, size, i);
            if (i == 0) {
                path.moveTo(corner.getX(), corner.getY()); // Verwende double Werte
            } else {
                path.lineTo(corner.getX(), corner.getY()); // Verwende double Werte
            }
        }
        path.closePath();
        return path;
    }

    @Override
    public Dimension getPreferredSize() {
        if (grid == null) {
            return new Dimension(600, 400);
        }
        // Die Berechnung von getPreferredSize bleibt vorerst unverändert,
        // da sie komplex ist und möglicherweise nicht das Hauptproblem darstellt.
        int effectiveWidth = grid.getWidth();
        int effectiveHeight = grid.getHeight();
        double hexWidth = 2.0 * hexSize;
        double horizSpacing = 1.5 * hexSize;
        int approxWidth = (int) Math.round((effectiveWidth > 0 ? (effectiveWidth - 1) * horizSpacing + hexWidth : hexWidth));
        double hexHeight = SQRT3 * hexSize;
        double vertSpacing = hexHeight;
        int approxHeight = (int) Math.round((effectiveHeight > 0 ? (effectiveHeight - 1) * vertSpacing + hexHeight: hexHeight));
        return new Dimension(approxWidth + 2 * padding, approxHeight + 2 * padding);
    }
}
