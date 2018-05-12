package tech.pauly.findapet.data.models;

import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum AnimalType {
    Dog(R.string.tab_dog, R.string.dog),
    Cat(R.string.tab_cat, R.string.cat),
    SmallFurry(R.string.tab_smallfurry, R.string.smallfurry),
    Barnyard(R.string.tab_barnyard, R.string.barnyard),
    Bird(R.string.tab_bird, R.string.bird),
    Horse(R.string.tab_horse, R.string.horse),
    Rabbit(R.string.tab_rabbit, R.string.rabbit),
    Reptile(R.string.tab_reptile, R.string.reptile);

    @StringRes
    private int tabName;

    @StringRes
    private int singularName;

    AnimalType(@StringRes int tabName, @StringRes int singularName) {
        this.tabName = tabName;
        this.singularName = singularName;
    }

    public int getTabName() {
        return tabName;
    }

    public int getSingularName() {
        return singularName;
    }
}
