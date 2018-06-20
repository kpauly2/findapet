package tech.pauly.findapet.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import io.reactivex.Single
import tech.pauly.findapet.data.models.Filter

@Dao
interface FilterDao {

    @Query("SELECT * FROM filter WHERE id=:id")
    fun findById(id: Long?): Single<Filter>

    @Insert
    fun insert(filter: Filter): Long
}
