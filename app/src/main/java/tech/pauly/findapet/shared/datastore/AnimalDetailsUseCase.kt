package tech.pauly.findapet.shared.datastore

import tech.pauly.findapet.data.models.Animal

open class AnimalDetailsUseCase(open val animal: Animal,
                                open val tab: Tab) : UseCase {
    enum class Tab {
        DETAILS, CONTACT
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnimalDetailsUseCase

        if (animal != other.animal) return false
        if (tab != other.tab) return false

        return true
    }

    override fun hashCode(): Int {
        var result = animal.hashCode()
        result = 31 * result + tab.hashCode()
        return result
    }

}