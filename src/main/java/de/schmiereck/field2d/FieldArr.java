package de.schmiereck.field2d;

import java.util.Arrays;
import java.util.stream.Stream;

public class FieldArr {
    private final Field[] fieldArr;

    public FieldArr(final int size) {
        this.fieldArr = new Field[size];
        for (int i = 0; i < size; i++) {
            this.fieldArr[i] = new Field();
        }
    }

    public Field[] getFieldArr() {
        return this.fieldArr;
    }

    public int getLength() {
        return this.fieldArr.length;
    }

    public Stream<Field> stream() {
        return Arrays.stream(this.fieldArr);
    }
}
