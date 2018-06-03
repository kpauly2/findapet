package tech.pauly.findapet.data;

import android.arch.persistence.room.TypeConverter;

import tech.pauly.findapet.data.models.Sex;

public class SexConverter {

    @TypeConverter
    public static Sex toSex(int code) {
        switch (code) {
            case 1:
                return Sex.M;
            case 2:
                return Sex.F;
            default:
                return Sex.U;
        }
    }

    @TypeConverter
    public static int toCode(Sex sex) {
        return sex.getCode();
    }
}
