package tech.pauly.old.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import tech.pauly.old.data.models.Converters
import tech.pauly.old.data.models.LocalAnimal
import tech.pauly.old.data.models.Shelter

@Database(entities = [LocalAnimal::class, Shelter::class], version = 3 )
@TypeConverters(Converters::class)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun shelterDao(): ShelterDao
}