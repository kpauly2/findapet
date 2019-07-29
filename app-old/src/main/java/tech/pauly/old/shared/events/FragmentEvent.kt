package tech.pauly.old.shared.events

import androidx.annotation.IdRes
import kotlin.reflect.KClass

open class FragmentEvent(private val emitter: Any,
                         open val fragment: KClass<*>,
                         @IdRes open val container: Int) : BaseViewEvent(emitter::class) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FragmentEvent

        if (emitter != other.emitter) return false
        if (fragment != other.fragment) return false
        if (container != other.container) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emitter.hashCode()
        result = 31 * result + fragment.hashCode()
        result = 31 * result + container
        return result
    }
}