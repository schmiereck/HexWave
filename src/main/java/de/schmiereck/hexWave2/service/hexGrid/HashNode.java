package de.schmiereck.hexWave2.service.hexGrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HashNode {
    final List<HashPart> hashPartList = new ArrayList<>();

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HashNode that = (HashNode) o;
        return Objects.equals(this.hashPartList, that.hashPartList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.hashPartList);
    }
 }
