package de.schmiereck.hexWave2.math;

import de.schmiereck.hexWave2.service.hexGrid.Cell;

import java.io.Serializable;

/**
 * Impuls ist das Produkt von Masse (mass) und Geschwindigkeit (velocity) eines Körpers
 * impulse = velocity * mass;
 *
 * Zentraler elastischer Stoß
 * v1' = (m1 * v1 + m2 * (2 * v2 - v1)) / (m1 + m2)
 * v2' = (m2 * v2 + m1 * (2 * v1 - v2)) / (m1 + m2)
 */
public class MoveHexVector extends HexVector implements Serializable {
    public Cell.Dir lastCheckedDir = Cell.Dir.AP;

}
