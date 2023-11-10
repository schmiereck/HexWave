package de.schmiereck.hexWave2.math;

/**
 * <code>
 *  denominator
 * -------------
 *  numerator
 * </code>
 */
public class NumService {
    private final int denominator;

    public NumService(final int denominator) {
        this.denominator = denominator;
    }

    public Num createNum() {
        return new Num(this.denominator);
    }

    public Num createNum(final Num num) {
        final Num newNum = this.createNum();
        final Num.NumSerie numSerie = this.getFirstNumSerie(num);
        final Num.NumSerie newNumSerie = this.getFirstNumSerie(newNum);

        for (int pos = 0; pos <= this.denominator * 2; pos++) {
            newNumSerie.setNumCnt(pos, numSerie.getNumCnt(pos));
        }
        return newNum;
    }

    // numerator
    public void divNum(final Num num, final int numerator) {
        final Num.NumSerie numSerie = this.getFirstNumSerie(num);
        numSerie.incNumCnt(numerator);
    }

    public void addDivNum(final Num num, int numPos) {
        final Num.NumSerie numSerie = this.getFirstNumSerie(num);
        numSerie.decNumCnt(numPos);
        numSerie.incNumCnt(numPos + numPos);
    }

    public void addDivNum(final Num aNum, final Num bNum, int numPos) {
        final Num.NumSerie aNumSerie = this.getFirstNumSerie(aNum);
        final Num.NumSerie bNumSerie = this.getFirstNumSerie(bNum);
        aNumSerie.setNumCnt(numPos, aNumSerie.getNumCnt(numPos) + bNumSerie.getNumCnt(numPos));
    }

    public long getNumCnt(final Num num, int numPos) {
        final Num.NumSerie numSerie = this.getFirstNumSerie(num);
        return numSerie.getNumCnt(numPos);
    }

    public void incDivNum(final Num num, int numPos) {
        final Num.NumSerie numSerie = this.getFirstNumSerie(num);
        numSerie.incNumCnt(numPos);
    }

    public boolean addNum(final Num aNum, final Num bNum) {
        final boolean retAdded;
        final Num.NumSerie aNumSerie = this.getFirstNumSerie(aNum);
        final Num.NumSerie bNumSerie = this.getFirstNumSerie(bNum);

        // numerator / denominator
        final long[] aFraction = this.calcFraction(aNumSerie);
        final long[] bFraction = this.calcFraction(bNumSerie);

        this.calcNinimizeFraction(aFraction);
        this.calcNinimizeFraction(bFraction);

        if (aFraction[1] > bFraction[1]) {
            this.calcIncreaseDenominatorCnt(bFraction, aFraction[1]);
        } else {
            if (bFraction[1] > aFraction[1]) {
                this.calcIncreaseDenominatorCnt(aFraction, bFraction[1]);
            }
        }

        // denominators equal?
        if (aFraction[1] == bFraction[1]) {
            //final long numerator = aFraction[0] + bFraction[0];
            final long numerator = Math.addExact(aFraction[0], bFraction[0]);
            // numerators <= max denominator * 2?
            if (numerator <= (this.denominator * 2)) {
                final long denominatorCnt = aFraction[1];

                this.clear(aNumSerie);

                //aFraction[0] = numerator;
                //aFraction[1] = denominatorCnt;
                //this.calcNinimizeFraction(aFraction);

                long numeratorCalc = numerator;
                long denominatorCalc = denominatorCnt;

                for (int pos = 1; pos < denominatorCnt; pos++) {
                    if( (numeratorCalc % this.denominator) != 0) {
                        // throw new RuntimeException(String.format("numerator %d not good.", numeratorCalc));
                        break;
                    }
                    numeratorCalc /= this.denominator;
                    denominatorCalc--;
                }

                if (numeratorCalc > (this.denominator * 2)) {
                    throw new RuntimeException(String.format("numerator %d not good.", numeratorCalc));
                }

                //aNumSerie.setNumCnt(1, denominatorCnt); // 1/2 * 1/2 * ...
                //aNumSerie.incNumCnt((int)numerator); // ... * numerator/2

                //aNumSerie.setNumCnt(1, denominatorCalc / this.denominator); // 1/2 * 1/2 * ...
                aNumSerie.setNumCnt(1, denominatorCalc - 1L); // 1/2 * 1/2 * ...
                aNumSerie.setNumCnt((int)numeratorCalc, 1); // ... * numerator/2
                retAdded = true;
            } else {
                //throw new RuntimeException(String.format("numerators a:%d + b:%d = %d > max denominator (denominatorCnt:%d).",
                //        aFraction[0], bFraction[0], numerator, aFraction[1]));
                retAdded = false;
            }
        } else {
            //throw new RuntimeException("denominators not equal.");
            retAdded = false;
        }
        return retAdded;
    }

    /**
     * numerator / denominator:<br/>
     * Wenn denominatorCnt von aNum & bNum ungleich
     * kann der Cnt erhöht werden in dem numerator & denominator beliebig oft mit this.denominator multipliziert werden.
     */
    private void calcIncreaseDenominatorCnt(long[] fraction, long targetDenominatorCnt) {
        while (fraction[1] < targetDenominatorCnt) {
            //fraction[0] *= this.denominator;
            fraction[0] = Math.multiplyExact(fraction[0], this.denominator);
            fraction[1]++;
        }
    }

    /**
     * numerator / denominator:<br/>
     * Testen, ob der numerator ohne Rest durch this.denominator dividiert werden kann,
     * denominatorCnt kann dann jeweils um einen decrementiert werden.
     */
    private void calcNinimizeFraction(final long[] fraction) {
        while (((fraction[0] % this.denominator) == 0) && (fraction[1] > 0)) {
            fraction[0] /= this.denominator;
            fraction[1]--;
        }
    }

    private void clear(final Num.NumSerie aNumSerie) {
        for (int numPos = 0; numPos <= this.denominator * 2; numPos++) {
            aNumSerie.setNumCnt(numPos, 0);
        }
    }

    /**
     * @return  fraction[0]:numerator / fraction[1]:denominatorCnt
     */
    private long[] calcFraction(Num.NumSerie numSerie) {
        final long[] fraction = new long[2];
        fraction[0] = 1L;

        for (int numPos = 0; numPos <= this.denominator * 2; numPos++) {
            final long aNumCnt = numSerie.getNumCnt(numPos);
            for (int cnt = 0; cnt < aNumCnt; cnt++) {
                //fraction[0] *= numPos;
                fraction[0] = Math.multiplyExact(fraction[0], numPos);
                fraction[1]++;
            }
        }
        return fraction;
    }

    public double calcNumber(final Num num) {
        double retNumber = this.denominator;
        final Num.NumSerie numSerie = this.getFirstNumSerie(num);

        for (int numPos = 0; numPos <= this.denominator * 2; numPos++) {
            final long numCnt = numSerie.getNumCnt(numPos);
            if (numCnt > 0L) {
                for (int pos = 0; pos < numCnt; pos++) {
                    retNumber = retNumber * ((double) numPos / (double) this.denominator);
                }
            }
        }
        return retNumber;
    }

    private Num.NumSerie getFirstNumSerie(final Num num) {
        final Num.NumSerie numSerie;
        if (num.getNumSerieListSize() == 0) {
            numSerie = new Num.NumSerie(this.denominator * 2 + 1);
            num.addNumSerie(numSerie);
        } else {
            numSerie = num.getNumSerie(0);
        }
        return numSerie;
    }

    public long calcNumeratorCnt(final Num num) {
        long reNumeratorCnt = 0;
        final Num.NumSerie numSerie = this.getFirstNumSerie(num);

        for (int numPos = 0; numPos <= this.denominator * 2; numPos++) {
            final long numCnt = numSerie.getNumCnt(numPos);
            if (numCnt > 0L) {
                reNumeratorCnt += numCnt;
            }
        }
        return reNumeratorCnt;
    }

    public int findSingleDiffNumPos(final Num aNum, final Num bNum) {
        int retDifNumPos = -1;
        final Num.NumSerie aNumSerie = this.getFirstNumSerie(aNum);
        final Num.NumSerie bNumSerie = this.getFirstNumSerie(bNum);

        for (int numPos = 0; numPos <= this.denominator * 2; numPos++) {
            final long aNumCnt = aNumSerie.getNumCnt(numPos);
            final long bNumCnt = bNumSerie.getNumCnt(numPos);

            if (aNumCnt != bNumCnt) {
                if (retDifNumPos == -1) {
                    // Found first diff
                    retDifNumPos = numPos;
                } else {
                    // Found second diff.
                    retDifNumPos = -1;
                    break;
                }
            }
        }
        return retDifNumPos;
    }
}
