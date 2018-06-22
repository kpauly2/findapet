package tech.pauly.findapet.shared.events

import kotlin.reflect.KClass

open class ActivityEvent(private val emitter: Any,
                         open val startActivity: KClass<*>? = null,
                         open val finishActivity: Boolean = false) : BaseViewEvent(emitter::class) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ActivityEvent

        if (emitter != other.emitter) return false
        if (startActivity != other.startActivity) return false
        if (finishActivity != other.finishActivity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emitter.hashCode()
        result = 31 * result + (startActivity?.hashCode() ?: 0)
        result = 31 * result + finishActivity.hashCode()
        return result
    }
}
