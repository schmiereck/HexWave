package de.schmiereck.oscillation1;

import java.awt.*;
import java.util.List;

public class AmplitudeGraph {
    final List<Integer> amplitudeValueList;
    final Color lineColor;

    public AmplitudeGraph(final List<Integer> amplitudeValueList, final Color lineColor) {
        this.amplitudeValueList = amplitudeValueList;
        this.lineColor = lineColor;
    }
}
