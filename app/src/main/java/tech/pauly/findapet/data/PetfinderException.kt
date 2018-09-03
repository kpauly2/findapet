package tech.pauly.findapet.data

import tech.pauly.findapet.data.models.StatusCode

open class PetfinderException(open val statusCode: StatusCode) : Exception() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PetfinderException

        if (statusCode != other.statusCode) return false

        return true
    }

    override fun hashCode(): Int {
        return statusCode.hashCode()
    }
}
