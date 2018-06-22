package tech.pauly.findapet.data.models

import android.arch.persistence.room.TypeConverter

class Converters {
    @TypeConverter
    fun toAge(code: Int): Age {
        return when (code) {
            1 -> Age.BABY
            2 -> Age.YOUNG
            3 -> Age.ADULT
            4 -> Age.SENIOR
            else -> Age.MISSING
        }
    }

    @TypeConverter
    fun toCode(age: Age): Int {
        return age.code
    }

    @TypeConverter
    fun toSize(code: Int): AnimalSize {
        return when (code) {
            1 -> AnimalSize.SMALL
            2 -> AnimalSize.MEDIUM
            3 -> AnimalSize.LARGE
            4 -> AnimalSize.EXTRA_LARGE
            else -> AnimalSize.MISSING
        }
    }

    @TypeConverter
    fun toCode(size: AnimalSize): Int {
        return size.code
    }

    @TypeConverter
    fun toSex(code: Int): Sex {
        return when (code) {
            1 -> Sex.MALE
            2 -> Sex.FEMALE
            else -> Sex.MISSING
        }
    }

    @TypeConverter
    fun toCode(sex: Sex): Int {
        return sex.code
    }
}