package tech.pauly.findapet.pets.network

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.GET
import tech.pauly.findapet.di.CoroutineContextProvider
import tech.pauly.findapet.auth.AuthRepository
import tech.pauly.findapet.pets.model.PetList

class PetsRepository(
    private val service: PetsService,
    private val auth: AuthRepository,
    private val scope: CoroutineContextProvider
) {

    suspend fun fetchPets(): PetList? {
        return withContext(scope.io) {
            val response = auth.doAuthorizedCall { service.fetchPetsAsync() }
            if (response.isSuccessful) {
                response.body()
            } else {
                throw HttpException(response)
            }
        }
    }
}

interface PetsService {
    @GET("animals")
    fun fetchPetsAsync(): Deferred<Response<PetList>>
}