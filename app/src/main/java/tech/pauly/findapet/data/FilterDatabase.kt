package tech.pauly.findapet.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

import tech.pauly.findapet.data.models.Converters
import tech.pauly.findapet.data.models.Filter

@Database(entities = [(Filter::class)], version = 6)
@TypeConverters(Converters::class)
abstract class FilterDatabase : RoomDatabase() {
    abstract fun filterDao(): FilterDao
}
