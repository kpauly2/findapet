package tech.pauly.findapet.dependencyinjection;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import tech.pauly.findapet.data.AnimalService;
import tech.pauly.findapet.data.BreedService;
import tech.pauly.findapet.data.FilterDatabase;
import tech.pauly.findapet.data.PetfinderEndpoints;

@Module
public class DataModule {

    @Provides
    AnimalService provideAnimalService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(PetfinderEndpoints.INSTANCE.getEndpoint())
                .client(client)
                .build()
                .create(AnimalService.class);
    }

    @Provides
    @Singleton
    FilterDatabase provideFilterDatabase(@ForApplication Context context) {
        return Room.databaseBuilder(context, FilterDatabase.class, "filter-database")
                   .fallbackToDestructiveMigration()
                   .build();
    }

    @Provides
    BreedService provideBreedService() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(PetfinderEndpoints.INSTANCE.getEndpoint())
                .client(client)
                .build()
                .create(BreedService.class);
    }
}
