package de.schmiereck.moveField2D;

import java.util.Arrays;
import java.util.stream.Stream;

public class MoveFieldArrDto {
    private final MoveFieldDto[] fieldDtoArr;

    public MoveFieldArrDto(final int size) {
        this.fieldDtoArr = new MoveFieldDto[size];
        for (int i = 0; i < size; i++) {
            this.fieldDtoArr[i] = new MoveFieldDto();
        }
    }

    public MoveFieldDto[] getFieldDtoArr() {
        return this.fieldDtoArr;
    }

    public void copyFrom(final MoveFieldArr fieldArr) {
        for (int pos = 0; pos < this.fieldDtoArr.length; pos++) {
            final MoveField field = fieldArr.getFieldArr()[pos];
            final MoveFieldDto fieldDto = this.fieldDtoArr[pos];
            fieldDto.value = field.field.value;
            fieldDto.outValue = field.field.outValue * field.probability;
            fieldDto.moveValue = field.moveField.value;
        }
    }

    public int getLength() {
        return this.fieldDtoArr.length;
    }

    public Stream<MoveFieldDto> stream() {
        return Arrays.stream(this.fieldDtoArr);
    }
}
