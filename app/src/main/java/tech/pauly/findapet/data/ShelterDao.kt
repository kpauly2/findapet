package tech.pauly.findapet.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import tech.pauly.findapet.data.models.Shelter

@Dao
interface ShelterDao {

    @Query("SELECT * FROM Shelter WHERE id = :shelterId")
    fun getShelter(shelterId: String): Single<Shelter>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(shelter: Shelter): Long

    @Query("DELETE FROM Shelter WHERE id = :shelterId")
    fun delete(shelterId: String): Int
}