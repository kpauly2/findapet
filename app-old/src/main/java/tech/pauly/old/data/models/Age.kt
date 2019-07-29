package tech.pauly.old.data.models

import androidx.annotation.StringRes
import tech.pauly.old.R

enum class Age (val code: Int,
                @StringRes val formattedName: Int,
                val serverName: String?) {

    MISSING(0, R.string.missing, null),
    BABY(1, R.string.baby, "Baby"),
    YOUNG(2, R.string.young, "Young"),
    ADULT(3, R.string.adult, "Adult"),
    SENIOR(4, R.string.senior, "Senior");

    companion object {
        fun fromString(name: String): Age {
            return Age.values().single { it.name.equals(name, true) }
        }
    }
}
