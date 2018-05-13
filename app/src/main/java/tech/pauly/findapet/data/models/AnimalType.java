package tech.pauly.findapet.data.models;

import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum AnimalType {
    DOG("Dog", R.string.tab_dog, R.string.dog),
    CAT("Cat", R.string.tab_cat, R.string.cat),
    SMALLFURRY("Small & Furry", R.string.tab_smallfurry, R.string.smallfurry),
    BARNYARD("Barnyard", R.string.tab_barnyard, R.string.barnyard),
    BIRD("Bird", R.string.tab_bird, R.string.bird),
    HORSE("Horse", R.string.tab_horse, R.string.horse),
    RABBIT("Rabbit", R.string.tab_rabbit, R.string.rabbit),
    REPTILE("Scales, Fins & Other", R.string.tab_reptile, R.string.reptile);

    private String serverName;

    @StringRes
    private int tabName;

    @StringRes
    private int singularName;

    AnimalType(String serverName, @StringRes int tabName, @StringRes int singularName) {
        this.serverName = serverName;
        this.tabName = tabName;
        this.singularName = singularName;
    }

    static AnimalType fromString(String name) {
        for (AnimalType type : AnimalType.values()) {
            if (type.serverName.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching AnimalType for name " + name);
    }

    public int getTabName() {
        return tabName;
    }

    public int getSingularName() {
        return singularName;
    }

    public String getServerName() {
        return serverName;
    }
}
