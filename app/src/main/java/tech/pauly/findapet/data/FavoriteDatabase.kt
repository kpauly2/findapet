package tech.pauly.findapet.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tech.pauly.findapet.data.models.Converters
import tech.pauly.findapet.data.models.LocalAnimal
import tech.pauly.findapet.data.models.Shelter

@Database(entities = [LocalAnimal::class, Shelter::class], version = 3 )
@TypeConverters(Converters::class)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun shelterDao(): ShelterDao
}