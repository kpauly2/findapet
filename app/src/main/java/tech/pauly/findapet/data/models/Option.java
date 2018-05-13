package tech.pauly.findapet.data.models;

import android.support.annotation.StringRes;

import tech.pauly.findapet.R;

public enum Option {
    SPECIAL_NEEDS("specialNeeds", R.string.special_needs),
    NO_DOGS("noDogs", R.string.no_dogs),
    NO_CATS("noCats", R.string.no_cats),
    NO_KIDS("noKids", R.string.no_kids),
    NO_CLAWS("noClaws", R.string.no_claws),
    HAS_SHOTS("hasShots", R.string.has_shots),
    HOUSE_BROKEN("houseBroken", R.string.house_broken),
    HOUSE_TRAINED("housetrained", R.string.house_trained),
    ALTERED("altered", R.string.altered);

    private String serverName;

    @StringRes
    private int formattedName;

    Option(String serverName, @StringRes int formattedName) {
        this.serverName = serverName;
        this.formattedName = formattedName;
    }

    @StringRes
    public int getFormattedName() {
        return formattedName;
    }

    public static Option fromString(String name) {
        for (Option option : Option.values()) {
            if (option.serverName.equalsIgnoreCase(name)) {
                return option;
            }
        }
        throw new IllegalArgumentException("No matching Option for name " + name);
    }
}
