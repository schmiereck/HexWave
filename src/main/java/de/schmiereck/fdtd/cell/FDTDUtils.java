package de.schmiereck.fdtd.cell;

public class FDTDUtils {

    static void copyToRenderBuffer(final FDTDCell[] uCurrent, final FDTDCellDto[] uRender) {
        for (int pos = 0; pos < uCurrent.length; pos++) {
            uRender[pos].uCurrent = uCurrent[pos].uCurrent;
            uRender[pos].uNext = uCurrent[pos].uNext;
            uRender[pos].uPrevious = uCurrent[pos].uPrevious;
        }
    }

    static void updateCurrentFromNextState(final FDTDCell[] uCurrent) {
        for (int pos = 0; pos < uCurrent.length; pos++) {
            uCurrent[pos].uPrevious = uCurrent[pos].uCurrent;
            uCurrent[pos].uCurrent = uCurrent[pos].uNext;
        }
    }
}
