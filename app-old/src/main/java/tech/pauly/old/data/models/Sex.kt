package tech.pauly.old.data.models

import androidx.annotation.StringRes
import tech.pauly.old.R
import tech.pauly.old.shared.SentencePlacement

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

    @StringRes
    fun getGrammaticalForm(place: SentencePlacement): Int {
        return when (this) {
            Sex.MALE -> {
                return when (place) {
                    SentencePlacement.SUBJECT -> R.string.pronoun_male_subject
                    SentencePlacement.OBJECT -> R.string.pronoun_male_object
                }
            }
            Sex.FEMALE -> {
                return when (place) {
                    SentencePlacement.SUBJECT -> R.string.pronoun_female_subject
                    SentencePlacement.OBJECT -> R.string.pronoun_female_object
                }
            }
            else -> R.string.pronoun_missing
        }
    }
}
