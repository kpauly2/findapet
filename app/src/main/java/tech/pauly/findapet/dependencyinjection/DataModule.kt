package tech.pauly.findapet.dependencyinjection

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import tech.pauly.findapet.data.AnimalService
import tech.pauly.findapet.data.BreedService
import tech.pauly.findapet.data.FilterDatabase
import tech.pauly.findapet.data.PetfinderEndpoints
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    internal fun provideAnimalService(): AnimalService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        return Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(PetfinderEndpoints.endpoint)
                .client(client)
                .build()
                .create(AnimalService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideFilterDatabase(@ForApplication context: Context): FilterDatabase {
        return Room.databaseBuilder(context, FilterDatabase::class.java, "filter-database")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    internal fun provideBreedService(): BreedService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        return Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(PetfinderEndpoints.endpoint)
                .client(client)
                .build()
                .create(BreedService::class.java)
    }
}
