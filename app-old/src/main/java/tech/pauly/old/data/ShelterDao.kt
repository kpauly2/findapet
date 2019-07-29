package tech.pauly.old.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single
import tech.pauly.old.data.models.Shelter

@Dao
interface ShelterDao {

    @Query("SELECT * FROM Shelter WHERE id = :shelterId")
    fun getShelter(shelterId: String): Single<Shelter>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(shelter: Shelter): Long

    @Query("DELETE FROM Shelter WHERE id = :shelterId")
    fun delete(shelterId: String): Int
}