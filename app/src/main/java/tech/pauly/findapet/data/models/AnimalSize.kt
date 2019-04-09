package tech.pauly.findapet.data.models

import androidx.annotation.StringRes
import tech.pauly.findapet.R

enum class AnimalSize(val code: Int,
                      @StringRes val formattedName: Int,
                      val serverName: String?) {

    MISSING(0, R.string.missing, null),
    SMALL(1, R.string.small, "S"),
    MEDIUM(2, R.string.medium, "M"),
    LARGE(3, R.string.large, "L"),
    EXTRA_LARGE(4, R.string.extra_large, "XL");

    companion object {
        fun fromString(name: String): AnimalSize {
            return AnimalSize.values().single { it.serverName.equals(name, true) }
        }
    }
}
