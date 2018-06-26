package tech.pauly.findapet.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import tech.pauly.findapet.data.models.Favorite

@Database(entities = [Favorite::class], version = 1)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}