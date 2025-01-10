package de.schmiereck.wavePacket;

import de.schmiereck.oscillation1.Holder;

import javax.swing.*;
import java.awt.*;

public class WavePacketGraphPanel  extends JPanel {
    final Holder<Integer> t;
    final double[] uRender;
    private final double maxFieldValue;

    public WavePacketGraphPanel(Holder<Integer> t, double[] uRender, final double maxFieldValue) {
        this.t = t;
        this.uRender = uRender;
        this.maxFieldValue = maxFieldValue;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);



        final int width = this.getWidth();
        final int height = this.getHeight();
        final int padding = 25;
        final int labelPadding = 25;

        final int amplitudeValueListSize = this.uRender.length;

        final int maxAmplitudeValue = (int)this.maxFieldValue;
        final int minAmplitudeValue = (int)-this.maxFieldValue;
        //final int amplitudeRange = roundTo(maxAmplitudeValue - minAmplitudeValue);
        final int amplitudeRange = Math.max(Math.abs(maxAmplitudeValue), Math.abs(minAmplitudeValue)) * 2;

        int pointWidth = 4;

        final double xScale = ((double) width - (2 * padding) - labelPadding) / (amplitudeValueListSize - 1);
        final double yScale = ((double) height - (2 * padding) - labelPadding) / amplitudeRange;

        // Draw white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(padding + labelPadding, padding, width - (2 * padding) - labelPadding, height - 2 * padding - labelPadding);
        g2d.setColor(Color.BLACK);

        // Create hatch marks and grid lines for y-axis.
        // Find out how many divisions to make, to show labels roundet on ten based positions depending on numberYDivisions.
        final int yTileCount = 10;
        final int yTileEvery = (amplitudeRange / yTileCount);
        final int yTileEvery2 = yTileEvery > 10 ? (yTileEvery / 5) : 1;
        for (int i = 0; i < amplitudeRange + 1; i++) {
            final int x0 = padding + labelPadding;
            final int x1 = pointWidth + padding + labelPadding;
            final int y0 = height - ((i * (height - padding * 2 - labelPadding)) / amplitudeRange + padding + labelPadding);
            final int y1 = y0;
            if ((amplitudeValueListSize > 0) &&
                    ((i % yTileEvery) == 0)) {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(padding + labelPadding + 1 + pointWidth, y0, width - padding, y1);
                g2d.setColor(Color.BLACK);
                //final String yLabel = ((int) ((minAmplitudeValue + amplitudeRange * ((i * 1.0) / amplitudeRange)) * 100)) / 100.0 + "";
                //final String yLabel = ((int) (((minAmplitudeValue + amplitudeRange) * ((i * 1.0) / amplitudeRange)) * 100)) / 100.0 + "";
                final String yLabel = (minAmplitudeValue + i) + "";
                final FontMetrics metrics = g2d.getFontMetrics();
                final int labelWidth = metrics.stringWidth(yLabel);
                g2d.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            if ((amplitudeValueListSize > 0) &&
                    ((i % yTileEvery2) == 0)) {
                g2d.drawLine(x0, y0, x1, y1);
            }
        }

        // Create hatch marks and grid lines for x-axis.
        final int xTileEvery = (int) ((amplitudeValueListSize / 20.0)) + 1;
        for (int i = 0; i < amplitudeValueListSize; i++) {
            if (amplitudeValueListSize > 1) {
                final int x0 = i * (width - padding * 2 - labelPadding) / (amplitudeValueListSize - 1) + padding + labelPadding;
                final int x1 = x0;
                final int y0 = height - padding - labelPadding;
                final int y1 = y0 - pointWidth;
                if ((i % xTileEvery) == 0) {
                    g2d.setColor(Color.LIGHT_GRAY);
                    g2d.drawLine(x0, height - padding - labelPadding - 1 - pointWidth, x1, padding);
                    g2d.setColor(Color.BLACK);
                    final String xLabel = i + "";
                    final FontMetrics metrics = g2d.getFontMetrics();
                    final int labelWidth = metrics.stringWidth(xLabel);
                    g2d.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2d.drawLine(x0, y0, x1, y1);
            }
        }

//        final Holder<Integer> index = new Holder<>(0);
//        this.fieldArrDto.stream().forEach(fieldDto -> {
//            final int pos = index.value++;
//            drawGraph(g2d, xScale, yScale, padding, labelPadding, maxAmplitudeValue, pointWidth, pos, fieldDto);
//        });
        for (int pos = 0; pos < this.uRender.length; pos++) {
            final double v = this.uRender[pos] * maxAmplitudeValue;
            drawGraph(g2d, xScale, yScale, padding, labelPadding, maxAmplitudeValue, pointWidth, pos, v);
        }
    }

    private void drawGraph(Graphics2D g2d, double xScale, double yScale, int padding, int labelPadding, int maxAmplitudeValue, int pointWidth,
                           final int pos, final double v) {
        final int x = (int) (pos * xScale + padding + labelPadding);
        {
            final int y = (int) ((maxAmplitudeValue - v) * yScale + padding);

            final int pw = pointWidth + 4;
            final int xPoint = x - pw / 2;
            final int yPoint = y - pw / 2;
            final int wPoint = pw;
            final int hPoint = pw;
            g2d.setColor(Color.ORANGE);
            g2d.drawOval(xPoint, yPoint, wPoint, hPoint);
        }
    }
}