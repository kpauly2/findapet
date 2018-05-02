package tech.pauly.findapet.dependencyinjection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import tech.pauly.findapet.data.AnimalService;
import tech.pauly.findapet.data.IoScheduler;
import tech.pauly.findapet.data.MainThreadScheduler;

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
                .baseUrl("http://api.petfinder.com/")
                .client(client)
                .build()
                .create(AnimalService.class);
    }

    @Provides
    @IoScheduler
    Scheduler provideIoScheduler() {
        return Schedulers.io();
    }

    @Provides
    @Singleton
    @MainThreadScheduler
    Scheduler provideMainScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
