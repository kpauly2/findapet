package tech.pauly.findapet.data.models;

import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum Age {
    BABY(R.string.baby),
    YOUNG(R.string.young),
    ADULT(R.string.adult),
    SENIOR(R.string.senior);

    @StringRes
    private int name;

    Age(@StringRes int name) {
        this.name = name;
    }

    static Age fromString(String name) {
        for (Age age : Age.values()) {
            if (age.name().equalsIgnoreCase(name)) {
                return age;
            }
        }
        throw new IllegalArgumentException("No matching Age for name " + name);
    }

    public int getName() {
        return name;
    }
}
