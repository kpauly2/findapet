package tech.pauly.findapet.data.models;

import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum Sex {
    M(R.string.male),
    F(R.string.female),
    U(R.string.unknown);

    private int formattedName;

    Sex(@StringRes int formattedName) {
        this.formattedName = formattedName;
    }

    public int getFormattedName() {
        return formattedName;
    }
}
