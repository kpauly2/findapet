package tech.pauly.findapet.data.models;

import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum Option {
    specialNeeds(R.string.special_needs),
    noDogs(R.string.no_dogs),
    noCats(R.string.no_cats),
    noKids(R.string.no_kids),
    noClaws(R.string.no_claws),
    hasShots(R.string.has_shots),
    houseBroken(R.string.house_broken),
    housetrained(R.string.house_trained),
    altered(R.string.altered);

    @StringRes
    private int formattedName;

    Option(@StringRes int formattedName) {
        this.formattedName = formattedName;
    }

    @StringRes
    public int getFormattedName() {
        return formattedName;
    }
}
