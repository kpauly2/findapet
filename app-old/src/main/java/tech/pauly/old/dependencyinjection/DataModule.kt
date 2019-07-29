package tech.pauly.old.dependencyinjection

import androidx.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import tech.pauly.old.data.*
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    internal fun provideAnimalService(): AnimalService {
        return petfinderRetrofit.create(AnimalService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideBreedService(): BreedService {
        return petfinderRetrofit.create(BreedService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideShelterService(): ShelterService {
        return petfinderRetrofit.create(ShelterService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideFilterDatabase(@ForApplication context: Context): FilterDatabase {
        return Room.databaseBuilder(context, FilterDatabase::class.java, "filter-database")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    @Singleton
    internal fun provideFavoriteDatabase(@ForApplication context: Context): FavoriteDatabase {
        return Room.databaseBuilder(context, FavoriteDatabase::class.java, "favorite-database")
                .fallbackToDestructiveMigration()
                .build()
    }

    private val petfinderRetrofit: Retrofit
        get() {
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
        }
}
