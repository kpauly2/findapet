package tech.pauly.findapet.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tech.pauly.findapet.BuildConfig
import tech.pauly.findapet.di.CoroutineContextProvider

class AuthRepository(
    private val service: AuthService,
    private val scope: CoroutineContextProvider
) {
    private val tokenRequestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("grant_type", "client_credentials")
        .addFormDataPart("client_id", BuildConfig.API_KEY)
        .addFormDataPart("client_secret", BuildConfig.SECRET)
        .build()

    suspend fun <T> doAuthorizedCall(call: () -> Deferred<Response<T>>): Response<T> {
        var response = call().await()
        if (response.code() == 401) {
            refreshAccessToken()
            response = call().await()
        }
        return response
    }

    private suspend fun refreshAccessToken() {
         withContext(scope.io) {
            val response = service.authorizeAsync(tokenRequestBody).await()
            if (response.isSuccessful) {
                response.body()?.accessToken?.let {
                    lastRefreshedTime = System.currentTimeMillis()
                    lastExpiryTimeMs = response.body()?.expiryTimeMs
                    lastAccessToken = it
                } ?: throw AccessTokenException()
            } else {
                throw HttpException(response)
            }
        }
    }

    companion object {
        var lastAccessToken: String? = null
        private var lastRefreshedTime: Long? = null
        private var lastExpiryTimeMs: Long? = null
    }
}

class PetfinderAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val original = chain.request()

        val request = original.newBuilder()
            .header("Authorization", "Bearer ${AuthRepository.lastAccessToken.orEmpty()}")
            .method(original.method(), original.body())
            .build()

        return chain.proceed(request)
    }
}

class AccessTokenException: Exception("There was an issue getting the access token")

interface AuthService {
    @POST("oauth2/token")
    fun authorizeAsync(@Body body: RequestBody): Deferred<Response<PetfinderAuthResponse>>
}

@JsonClass(generateAdapter = true)
class PetfinderAuthResponse(
    @Json(name = "token_type") val tokenType: String?,
    @Json(name = "expires_in") val expiryTimeMs: Long?,
    @Json(name = "access_token") val accessToken: String?
)