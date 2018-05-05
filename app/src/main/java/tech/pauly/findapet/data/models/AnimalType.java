package tech.pauly.findapet.data.models;

import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum AnimalType {
    Dog(R.string.tab_dog),
    Cat(R.string.tab_cat),
    SmallFurry(R.string.tab_smallfurry),
    Barnyard(R.string.tab_barnyard),
    Bird(R.string.tab_bird),
    Horse(R.string.tab_horse),
    Rabbit(R.string.tab_rabbit),
    Reptile(R.string.tab_reptile);

    @StringRes
    private int formattedName;

    AnimalType(@StringRes int formattedName) {
        this.formattedName = formattedName;
    }

    public int getFormattedName() {
        return formattedName;
    }
}
