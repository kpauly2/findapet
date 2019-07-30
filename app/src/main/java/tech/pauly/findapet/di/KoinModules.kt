package tech.pauly.findapet.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import tech.pauly.findapet.auth.AuthRepository
import tech.pauly.findapet.auth.AuthService
import tech.pauly.findapet.auth.PetfinderAuthInterceptor
import tech.pauly.findapet.pets.PetsViewModel
import tech.pauly.findapet.pets.network.PetsRepository
import tech.pauly.findapet.pets.network.PetsService
import java.util.*
import kotlin.coroutines.CoroutineContext

val authModule = module {
    single { AuthRepository(get(), CoroutineContextProvider()) }
    single { retrofitClient.create(AuthService::class.java) }
}

val petsModule = module {
    viewModel { PetsViewModel(get(), CoroutineContextProvider()) }
    single { PetsRepository(get(), get(), CoroutineContextProvider()) }
    single { retrofitClient.create(PetsService::class.java) }
}

private val retrofitClient: Retrofit
    get() {
        return Retrofit.Builder()
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(Date::class.java, Rfc3339DateJsonAdapter())
                        .build()
                )
            )
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        })
                    .addInterceptor(PetfinderAuthInterceptor())
                    .build()
            )
            .baseUrl("https://api.petfinder.com/v2/")
            .build()
    }

open class CoroutineContextProvider {
    open val android: CoroutineContext by lazy { Dispatchers.Main }
    open val io: CoroutineContext by lazy { Dispatchers.IO }
}