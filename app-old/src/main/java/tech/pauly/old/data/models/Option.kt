package tech.pauly.old.data.models


import androidx.annotation.StringRes
import tech.pauly.old.R

enum class Option(private val serverName: String,
                  @StringRes val formattedName: Int) {

    SPECIAL_NEEDS("specialNeeds", R.string.special_needs),
    NO_DOGS("noDogs", R.string.no_dogs),
    NO_CATS("noCats", R.string.no_cats),
    NO_KIDS("noKids", R.string.no_kids),
    NO_CLAWS("noClaws", R.string.no_claws),
    HAS_SHOTS("hasShots", R.string.has_shots),
    HOUSE_BROKEN("houseBroken", R.string.house_broken),
    HOUSE_TRAINED("housetrained", R.string.house_trained),
    ALTERED("altered", R.string.altered);

    companion object {
        fun fromString(name: String): Option {
            return Option.values().single { it.serverName.equals(name, true) }
        }
    }
}
