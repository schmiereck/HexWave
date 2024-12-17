package de.schmiereck.oscillation1;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AmplitudeGraphPanel extends JPanel {
    private final List<AmplitudeGraph> amplitudeGraphList;

    public AmplitudeGraphPanel(final List<AmplitudeGraph> amplitudeGraphList) {
        this.amplitudeGraphList = amplitudeGraphList;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int width = getWidth();
        final int height = getHeight();
        final int padding = 25;
        final int labelPadding = 25;

        final int amplitudeValueListSize = calcMaxAmplitudeValueListSize(this.amplitudeGraphList);

        final int maxAmplitudeValue = roundTo(calcMaxAmplitudeValue(this.amplitudeGraphList));
        final int minAmplitudeValue = roundTo(calcMinAmplitudeValue(this.amplitudeGraphList));
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
        final int yTileEvery2 = (yTileEvery / 5);
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
        //final AtomicInteger index = new AtomicInteger(0);
        final Holder<Integer> index = new Holder<>(0);

        this.amplitudeGraphList.forEach(amplitudeGraph -> {
            //final int pos = index.getAndIncrement();
            final int pos = index.value++;
            final String name = amplitudeGraph.name;
            final FontMetrics metrics = g2d.getFontMetrics();
            final Rectangle2D stringBounds = metrics.getStringBounds(name, g2d);
            final int nameHeight = (int) stringBounds.getHeight();

            final int x = padding + labelPadding + 10;
            final int y = height - (padding + labelPadding + 10 + (pos * nameHeight));

            g2d.setColor(amplitudeGraph.lineColor);
            g2d.fillRect(x, y - nameHeight + metrics.getDescent(), 10, nameHeight);

            g2d.setColor(Color.BLACK);
            g2d.drawString(name,
                    x + 16, y);
        });

        this.amplitudeGraphList.forEach(amplitudeGraph -> {
            drawGraph(g2d, xScale, yScale, padding, labelPadding, maxAmplitudeValue, pointWidth, amplitudeGraph.amplitudeValueList, amplitudeGraph.lineColor);
        });
    }

    private int roundTo(final int value) {
        final int base = 10;
        return roundToBase(value, base);
    }

    private int roundToBase(final int value, final int base) {
        final int b;
        if (value > 0) {
            b = base;
        } else {
            b = -base;
        }
        return ((value + b / 2) / base * base) + b;
    }

    private void drawGraph(final Graphics2D g2d,
                           final double xScale, final double yScale,
                           final int padding, final int labelPadding, final int maxAmplitudeValue,
                           final int pointWidth,
                           final List<Integer> amplitudeValues,
                           final Color lineColor) {
        final List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < amplitudeValues.size(); i++) {
            final int x = (int) (i * xScale + padding + labelPadding);
            final int y = (int) ((maxAmplitudeValue - amplitudeValues.get(i)) * yScale + padding);
            graphPoints.add(new Point(x, y));
        }

        // Draw the line
        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(2f));
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            final int x1 = graphPoints.get(i).x;
            final int y1 = graphPoints.get(i).y;
            final int x2 = graphPoints.get(i + 1).x;
            final int y2 = graphPoints.get(i + 1).y;
            g2d.drawLine(x1, y1, x2, y2);
        }

        // Draw points
        g2d.setColor(Color.RED);
        for (int i = 0; i < graphPoints.size(); i++) {
            final int x = graphPoints.get(i).x - pointWidth / 2;
            final int y = graphPoints.get(i).y - pointWidth / 2;
            final int ovalW = pointWidth;
            final int ovalH = pointWidth;
            g2d.fillOval(x, y, ovalW, ovalH);
        }
    }

    private int calcMinAmplitudeValue(final List<AmplitudeGraph> amplitudeGraphList) {
        //final AmplitudeGraph amplitudeGraph = amplitudeGraphList.get(0);
        //final List<Integer> amplitudeValues = amplitudeGraph.amplitudeValueList;
        //return amplitudeValues.stream().min(Integer::compareTo).orElse(0);
        return amplitudeGraphList.stream().
                map(amplitudeGraph -> amplitudeGraph.amplitudeValueList.stream()).
                map(amplitudeValueStream -> amplitudeValueStream.min(Integer::compareTo).orElse(0)).
                min(Integer::compareTo).orElse(0);
    }

    private int calcMaxAmplitudeValue(final List<AmplitudeGraph> amplitudeGraphList) {
        //final AmplitudeGraph amplitudeGraph = amplitudeGraphList.get(0);
        //final List<Integer> amplitudeValues = amplitudeGraph.amplitudeValueList;
        //return amplitudeValues.stream().max(Integer::compareTo).orElse(0);
        return amplitudeGraphList.stream().
                map(amplitudeGraph -> amplitudeGraph.amplitudeValueList.stream()).
                map(amplitudeValueStream -> amplitudeValueStream.max(Integer::compareTo).orElse(0)).
                max(Integer::compareTo).orElse(0);
    }

    private int calcMaxAmplitudeValueListSize(final List<AmplitudeGraph> amplitudeGraphList) {
        //final AmplitudeGraph amplitudeGraph = this.amplitudeGraphList.get(0);
        //final List<Integer> amplitudeValues = amplitudeGraph.amplitudeValueList;
        //final int amplitudeValueListSize = amplitudeValues.size();
        //return amplitudeValueListSize;
        return amplitudeGraphList.stream().
                map(amplitudeGraph -> amplitudeGraph.amplitudeValueList).
                map(amplitudeValueList -> amplitudeValueList.size()).
                max(Integer::compareTo).orElse(0);
    }

}
