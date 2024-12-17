package de.schmiereck.oscillation1;

import java.awt.*;
import java.util.List;

public class AmplitudeGraph {
    final String name;
    final Color lineColor;
    final List<Integer> amplitudeValueList;

    public AmplitudeGraph(final String name, final Color lineColor, final List<Integer> amplitudeValueList) {
        this.name = name;
        this.lineColor = lineColor;
        this.amplitudeValueList = amplitudeValueList;
    }
}
