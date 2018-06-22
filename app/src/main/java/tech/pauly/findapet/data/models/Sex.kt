package tech.pauly.findapet.data.models

import android.support.annotation.StringRes
import tech.pauly.findapet.R

enum class Sex(val code: Int,
               @StringRes val formattedName: Int,
               val serverName: String?) {

    MISSING(0, R.string.missing, null),
    MALE(1, R.string.male, "M"),
    FEMALE(2, R.string.female, "F"),
    UNKNOWN(3, R.string.unknown, "U");

    companion object {
        fun fromString(name: String): Sex {
                return Sex.values().single { it.serverName.equals(name, true) }
        }
    }
}
