package tech.pauly.findapet.shared.events

enum class OptionsMenuState {
    DISCOVER, EMPTY
}

open class OptionsMenuEvent(private val emitter: Any,
                            open val state: OptionsMenuState) : BaseViewEvent(emitter::class) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OptionsMenuEvent

        if (emitter != other.emitter) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emitter.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }
}