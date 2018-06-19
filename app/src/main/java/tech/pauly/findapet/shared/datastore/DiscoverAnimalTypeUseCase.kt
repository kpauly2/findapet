package tech.pauly.findapet.shared.datastore

import tech.pauly.findapet.data.models.AnimalType

open class DiscoverAnimalTypeUseCase(open val animalType: AnimalType) : UseCase {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiscoverAnimalTypeUseCase

        if (animalType != other.animalType) return false

        return true
    }

    override fun hashCode(): Int {
        return animalType.hashCode()
    }
}
