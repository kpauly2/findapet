package tech.pauly.findapet.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import tech.pauly.findapet.data.models.Converters
import tech.pauly.findapet.data.models.Filter

@Database(entities = [(Filter::class)], version = 6)
@TypeConverters(Converters::class)
abstract class FilterDatabase : RoomDatabase() {
    abstract fun filterDao(): FilterDao
}
