package de.schmiereck.hexWave.math;

import java.util.ArrayList;
import java.util.List;

public class Num {
    public static class NumSerie {
        private final long[] numArr;

        public NumSerie(final int numSize) {
            this.numArr = new long[numSize];
        }

        public void incNumCnt(final int numPos) {
            this.numArr[numPos]++;
        }

        public void decNumCnt(final int numPos) {
            this.numArr[numPos]--;
        }

        public void setNumCnt(final int numPos, final long cnt) {
            this.numArr[numPos] = cnt;
        }

        public long getNumCnt(int numPos) {
            return this.numArr[numPos];
        }
    }

    final int numSize;

    private final List<NumSerie> numSerieList = new ArrayList<>();

    public Num(final int numSize) {
        this.numSize = numSize;
    }

    public int getNumSerieListSize() {
        return this.numSerieList.size();
    }

    public void addNumSerie(final NumSerie numSerie) {
        this.numSerieList.add(numSerie);
    }
    public NumSerie getNumSerie(final int pos) {
        return this.numSerieList.get(pos);
    }

}
