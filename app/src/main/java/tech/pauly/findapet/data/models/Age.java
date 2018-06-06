package tech.pauly.findapet.data.models;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum Age {
    MISSING(0, R.string.missing, null),
    BABY(1, R.string.baby, "Baby"),
    YOUNG(2, R.string.young, "Young"),
    ADULT(3, R.string.adult, "Adult"),
    SENIOR(4, R.string.senior, "Senior");

    private int code;
    @StringRes
    private int formattedName;
    private String serverName;

    Age(int code, @StringRes int formattedName, @Nullable String serverName) {
        this.code = code;
        this.formattedName = formattedName;
        this.serverName = serverName;
    }

    public int getCode() {
        return code;
    }

    @Nullable
    public String getServerName() {
        return serverName;
    }

    @StringRes
    public int getFormattedName() {
        return formattedName;
    }

    static Age fromString(String name) {
        for (Age age : Age.values()) {
            if (age.name().equalsIgnoreCase(name)) {
                return age;
            }
        }
        throw new IllegalArgumentException("No matching Age for name " + name);
    }

    @TypeConverter
    public static Age toAge(int code) {
        switch (code) {
            case 1:
                return BABY;
            case 2:
                return YOUNG;
            case 3:
                return ADULT;
            case 4:
                return SENIOR;
            default:
                return MISSING;
        }
    }

    @TypeConverter
    public static int toCode(Age age) {
        return age.getCode();
    }
}
