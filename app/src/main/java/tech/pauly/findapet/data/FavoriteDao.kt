package tech.pauly.findapet.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Single
import tech.pauly.findapet.data.models.Favorite

@Dao
interface FavoriteDao {

    @Query("SELECT EXISTS(SELECT id FROM favorite WHERE animalId = :animalId)")
    fun isFavorited(animalId: Int): Single<Boolean>

    @Insert
    fun insert(favorite: Favorite): Long

    @Query("DELETE FROM favorite WHERE animalId= :animalId")
    fun delete(animalId: Int): Int
}