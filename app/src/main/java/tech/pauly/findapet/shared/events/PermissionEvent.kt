package tech.pauly.findapet.shared.events

import tech.pauly.findapet.shared.PermissionRequestResponse
import java.util.*

open class PermissionEvent(private val emitter: Any,
                           open val permissions: Array<String>,
                           open val listener: PermissionListener,
                           open val requestCode: Int) : BaseViewEvent(emitter.javaClass) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PermissionEvent

        if (emitter != other.emitter) return false
        if (!Arrays.equals(permissions, other.permissions)) return false
        if (listener != other.listener) return false
        if (requestCode != other.requestCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = emitter.hashCode()
        result = 31 * result + Arrays.hashCode(permissions)
        result = 31 * result + listener.hashCode()
        result = 31 * result + requestCode
        return result
    }
}

interface PermissionListener {
    fun onPermissionResult(response: PermissionRequestResponse)
}