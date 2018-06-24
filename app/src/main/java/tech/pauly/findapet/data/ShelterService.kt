package tech.pauly.findapet.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import tech.pauly.findapet.data.models.ShelterListResponse

interface ShelterService {
    @GET("shelter.find")
    fun fetchShelters(@Query("location") location: String,
                      @Query("key") key: String,
                      @Query("count") count: Int): Single<ShelterListResponse>
}