package de.schmiereck.moveField2D;

import java.util.Arrays;
import java.util.stream.Stream;

public class MoveFieldArr {
    private MoveField[] fieldArr;

    public MoveFieldArr(final int size) {
        this.fieldArr = new MoveField[size];
        for (int i = 0; i < size; i++) {
            this.fieldArr[i] = new MoveField();
        }
    }

    public MoveField[] getFieldArr() {
        return this.fieldArr;
    }

    public int getLength() {
        return this.fieldArr.length;
    }

    public Stream<MoveField> stream() {
        return Arrays.stream(this.fieldArr);
    }

    public void setFieldArr(final MoveField[] fieldArr) {
        this.fieldArr = fieldArr;
    }
}
