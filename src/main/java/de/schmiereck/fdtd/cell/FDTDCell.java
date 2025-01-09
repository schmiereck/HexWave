package de.schmiereck.fdtd.cell;

/**
 * Felder für die Zustände der Welle.
 */
public class FDTDCell {
    /**
     * Zustand zur aktuellen Zeit t.
     */
    int uCurrent;

    /**
     * Zustand zur Zeit t-Δt.
     */
    int uPrevious;

    /**
     * Zustand zur Zeit t+Δt.
     */
    int uNext;
}
