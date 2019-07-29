package tech.pauly.old.data.models

open class FetchAnimalsRequest(open val animalType: AnimalType,
                               open val lastOffset: Int,
                               open val location: String,
                               open val filter: Filter) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FetchAnimalsRequest

        if (animalType != other.animalType) return false
        if (lastOffset != other.lastOffset) return false
        if (location != other.location) return false
        if (filter != other.filter) return false

        return true
    }

    override fun hashCode(): Int {
        var result = animalType.hashCode()
        result = 31 * result + lastOffset
        result = 31 * result + location.hashCode()
        result = 31 * result + filter.hashCode()
        return result
    }
}
