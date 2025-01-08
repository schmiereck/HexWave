package de.schmiereck.field2D;

import java.util.Arrays;
import java.util.stream.Stream;

public class FieldArrDto {
    private final FieldDto[] fieldDtoArr;

    public FieldArrDto(final int size) {
        this.fieldDtoArr = new FieldDto[size];
        for (int i = 0; i < size; i++) {
            this.fieldDtoArr[i] = new FieldDto();
        }
    }

    public FieldDto[] getFieldDtoArr() {
        return this.fieldDtoArr;
    }

    public void copyFrom(final FieldArr fieldArr) {
        for (int pos = 0; pos < this.fieldDtoArr.length; pos++) {
            final Field field = fieldArr.getFieldArr()[pos];
            final FieldDto fieldDto = this.fieldDtoArr[pos];
            fieldDto.value = field.value;
            fieldDto.outValue = field.outValue;
        }
    }

    public int getLength() {
        return this.fieldDtoArr.length;
    }

    public Stream<FieldDto> stream() {
        return Arrays.stream(this.fieldDtoArr);
    }
}
