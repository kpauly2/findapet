package tech.pauly.findapet.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import tech.pauly.findapet.data.models.AnimalListResponse
import tech.pauly.findapet.data.models.SingleAnimalResponse

interface AnimalService {

    @GET("pet.find")
    fun fetchAnimals(@Query("location") location: String,
                     @Query("key") key: String,
                     @Query("animal") type: String,
                     @Query("offset") offset: Int,
                     @Query("count") count: Int,
                     @Query("sex") sex: String?,
                     @Query("age") age: String?,
                     @Query("size") size: String?,
                     @Query("breed") breed: String): Single<AnimalListResponse>

    @GET("pet.get")
    fun fetchAnimal(@Query("key") key: String,
                    @Query("id") id: String): Single<SingleAnimalResponse>
}
