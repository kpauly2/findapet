package tech.pauly.findapet.shared.events

import androidx.annotation.StringRes

open class SnackbarEvent(private val emitter: Any,
                         @StringRes val text: Int) : BaseViewEvent(emitter::class) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SnackbarEvent

        if (emitter != other.emitter) return false
        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emitter.hashCode()
        result = 31 * result + text
        return result
    }
}
