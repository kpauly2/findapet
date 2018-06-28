package tech.pauly.findapet.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import io.reactivex.Single
import tech.pauly.findapet.data.models.LocalAnimal

@Dao
interface FavoriteDao {

    @Query("SELECT EXISTS(SELECT id FROM LocalAnimal WHERE id = :animalId)")
    fun isFavorited(animalId: Int): Single<Boolean>

    @Insert
    fun insert(favorite: LocalAnimal): Long

    @Query("DELETE FROM LocalAnimal WHERE id = :animalId")
    fun delete(animalId: Int): Int

    @Query("SELECT * FROM LocalAnimal")
    fun getAll(): Single<List<LocalAnimal>>
}