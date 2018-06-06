package tech.pauly.findapet.data.models;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum Sex {
    MISSING(0, R.string.missing, null),
    MALE(1, R.string.male, "M"),
    FEMALE(2, R.string.female, "F"),
    UNKNOWN(3, R.string.unknown, "U");

    private int code;
    @StringRes
    private int formattedName;
    @Nullable
    private String serverName;

    Sex(int code, @StringRes int formattedName, @Nullable String serverName) {
        this.code = code;
        this.formattedName = formattedName;
        this.serverName = serverName;
    }

    public int getCode() {
        return code;
    }

    @StringRes
    public int getFormattedName() {
        return formattedName;
    }

    @Nullable
    public String getServerName() {
        return serverName;
    }

    @TypeConverter
    public static Sex toSex(int code) {
        switch (code) {
            case 1:
                return Sex.MALE;
            case 2:
                return Sex.FEMALE;
            default:
                return Sex.MISSING;
        }
    }

    @TypeConverter
    public static int toCode(Sex sex) {
        return sex.getCode();
    }

    public static Sex fromString(String name) {
        for (Sex sex : Sex.values()) {
            if (sex.serverName != null && sex.serverName.equals(name)) {
                return sex;
            }
        }
        throw new IllegalArgumentException("No matching Sex for name " + name);

    }
}
