package tech.pauly.findapet.data.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
open class Favorite(@PrimaryKey(autoGenerate = true)
                    open var id: Long? = null,
                    open var animalId: Int = 0,
                    open var name: String? = null,
                    open var imageLocation: String? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Favorite

        if (id != other.id) return false
        if (animalId != other.animalId) return false
        if (name != other.name) return false
        if (imageLocation != other.imageLocation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + animalId
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (imageLocation?.hashCode() ?: 0)
        return result
    }
}