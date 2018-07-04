package tech.pauly.findapet.data.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity
open class Filter(@PrimaryKey(autoGenerate = true)
                  open var id: Long? = null,
                  open var sex: Sex = Sex.MISSING,
                  open var age: Age = Age.MISSING,
                  open var size: AnimalSize = AnimalSize.MISSING,
                  open var breed: String = "") {

    @Ignore constructor() : this(null, Sex.MISSING, Age.MISSING, AnimalSize.MISSING, "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Filter

        if (id != other.id) return false
        if (sex != other.sex) return false
        if (age != other.age) return false
        if (size != other.size) return false
        if (breed != other.breed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + sex.hashCode()
        result = 31 * result + age.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + breed.hashCode()
        return result
    }
}