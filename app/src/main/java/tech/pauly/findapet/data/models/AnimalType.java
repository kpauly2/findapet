package tech.pauly.findapet.data.models;

import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum AnimalType {
    DOG("Dog", R.string.tab_dog),
    CAT("Cat", R.string.tab_cat),
    SMALLFURRY("Small & Furry", R.string.tab_smallfurry),
    BARNYARD("Barnyard", R.string.tab_barnyard),
    BIRD("Bird", R.string.tab_bird),
    HORSE("Horse", R.string.tab_horse),
    RABBIT("Rabbit", R.string.tab_rabbit),
    REPTILE("Scales, Fins & Other", R.string.tab_reptile);

    private String serverName;

    @StringRes
    private int toolbarName;

    AnimalType(String serverName, @StringRes int toolbarName) {
        this.serverName = serverName;
        this.toolbarName = toolbarName;
    }

    public String getServerName() {
        return name().toLowerCase();
    }

    static AnimalType fromString(String name) {
        for (AnimalType type : AnimalType.values()) {
            if (type.serverName.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching AnimalType for name " + name);
    }

    public int getToolbarName() {
        return toolbarName;
    }
}
