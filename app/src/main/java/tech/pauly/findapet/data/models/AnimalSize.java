package tech.pauly.findapet.data.models;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum AnimalSize {
    MISSING(0, R.string.missing, null),
    SMALL(1, R.string.small, "S"),
    MEDIUM(2, R.string.medium, "M"),
    LARGE(3, R.string.large, "L"),
    EXTRA_LARGE(4, R.string.extra_large, "XL");

    private int code;
    @StringRes
    private int formattedName;
    @Nullable
    private String serverName;

    AnimalSize(int code, @StringRes int formattedName, @Nullable String serverName) {
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
    public static AnimalSize toSize(int code) {
        switch (code) {
            case 1:
                return SMALL;
            case 2:
                return MEDIUM;
            case 3:
                return LARGE;
            case 4:
                return EXTRA_LARGE;
            default:
                return MISSING;
        }
    }

    @TypeConverter
    public static int toCode(AnimalSize size) {
        return size.getCode();
    }

    public static AnimalSize fromString(String name) {
        for (AnimalSize size : AnimalSize.values()) {
            if (size.serverName != null && size.serverName.equals(name)) {
                return size;
            }
        }
        throw new IllegalArgumentException("No matching AnimalSize for name " + name);
    }
}
