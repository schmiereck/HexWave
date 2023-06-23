package de.schmiereck.hexWave.service.life;

import de.schmiereck.hexWave.service.hexGrid.FieldType;
import de.schmiereck.hexWave.service.hexGrid.HexGridService;

import org.springframework.stereotype.Component;

@Component
public class FieldTypeService {
    public enum FieldTypeEnum {
        Part1(0),
        Part2(1),
        Part3(2),
        Com(3),
        PartPull(4),
        PartPush(5),
        Sun(6);

        public int no;

        FieldTypeEnum(final int no) {
            this.no = no;
        }
    }

    private FieldType[] fieldTypeArr;

    public FieldTypeService() {
        this.fieldTypeArr = new FieldType[FieldTypeEnum.values().length];

        this.fieldTypeArr[FieldTypeEnum.Part1.no] = new FieldType(3, false);    // Part
        this.fieldTypeArr[FieldTypeEnum.Part2.no] = new FieldType(3, false);    // Part
        this.fieldTypeArr[FieldTypeEnum.Part3.no] = new FieldType(3, false);    // Part
        this.fieldTypeArr[FieldTypeEnum.Com.no] = new FieldType(3, false);    // Part
        this.fieldTypeArr[FieldTypeEnum.PartPull.no] = new FieldType(3, false);
        this.fieldTypeArr[FieldTypeEnum.PartPush.no] = new FieldType(1, false);
        this.fieldTypeArr[FieldTypeEnum.Sun.no] = new FieldType(0, true);    // Sun
    }

    public FieldType getFieldType(final FieldTypeEnum fieldTypeEnum) {
        return this.fieldTypeArr[fieldTypeEnum.no];
    }

    public FieldType getFieldType(final int fieldTypeNo) {
        return this.fieldTypeArr[fieldTypeNo];
    }
}
