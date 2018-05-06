package tech.pauly.findapet.data.models;

import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum AnimalSize {
    S(R.string.small),
    M(R.string.medium),
    L(R.string.large),
    XL(R.string.extra_large);

    @StringRes
    private int formattedName;

    AnimalSize(@StringRes int formattedName) {
        this.formattedName = formattedName;
    }

    @StringRes
    public int getFormattedName() {
        return formattedName;
    }
}
