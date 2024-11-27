package de.schmiereck.hexWave2.service.hexGrid;

import de.schmiereck.hexWave2.math.ProbabilityVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * In:
 *  Part
 *      impulse
 *  Neighbour-Nodes
 *      all (fields) from other parts
 *
 * Out:
 *  for every posible result
 *      Node and Neighbour-Nodes
 *          resulting parts with their impulses
 *
 * Fields are not divided in separate possible branches,
 * their division only results in new field strength.
 *
 * Parts are divided into multiple possibility branches
 * if the impulse-vector have more than one result.
 *
 * Identity:
 *  Field:
 *      - particle
 *      - direct parent field-part (?)
 *  Part:
 *      - particle
 *
 * Hash:
 *  Part:
 *      - Part-Type
 *      - impulse-vector
 *      - possibility (strength for Field-Parts, Particle-Parts are always 100%)
 */
public class HashNodeArea {
    final HashNode[] inHashNodeArr = new HashNode[Cell.Dir.values().length + 1];

    final List<HashNodeArea> outHashNodeArealist = new ArrayList<>();

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HashNodeArea that = (HashNodeArea) o;
        return Arrays.equals(this.inHashNodeArr, that.inHashNodeArr);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.inHashNodeArr);
    }
}
