package tech.pauly.findapet.shared.datastore

import tech.pauly.findapet.data.models.Animal

open class AnimalDetailsUseCase(open val animal: Animal) : UseCase {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnimalDetailsUseCase

        if (animal != other.animal) return false

        return true
    }

    override fun hashCode(): Int {
        return animal.hashCode()
    }
}