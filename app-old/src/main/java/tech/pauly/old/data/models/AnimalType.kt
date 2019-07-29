package tech.pauly.old.data.models

import androidx.annotation.StringRes
import tech.pauly.old.R

enum class AnimalType(val serverName: String,
                      @StringRes val toolbarName: Int) {

    DOG("dog", R.string.tab_dog),
    CAT("cat", R.string.tab_cat),
    SMALL_FURRY("smallfurry", R.string.tab_smallfurry),
    BARNYARD("barnyard", R.string.tab_barnyard),
    BIRD("bird", R.string.tab_bird),
    HORSE("horse", R.string.tab_horse),
    RABBIT("rabbit", R.string.tab_rabbit),
    REPTILE("reptile", R.string.tab_reptile);

    companion object {
        fun fromString(name: String): AnimalType {
            return AnimalType.values().single { it.name.equals(name, true) }
        }
    }
}
