package tech.pauly.findapet.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import tech.pauly.findapet.data.models.BreedListResponse

interface BreedService {

    @GET("breed.list")
    fun fetchBreeds(@Query("key") key: String,
                    @Query("animal") type: String): Single<BreedListResponse>
}
