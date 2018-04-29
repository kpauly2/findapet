package tech.pauly.findapet.dependencyinjection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import tech.pauly.findapet.data.AnimalService;
import tech.pauly.findapet.data.IOScheduler;
import tech.pauly.findapet.data.MainThreadScheduler;

@Module
public class DataModule {

    @Provides
    AnimalService provideAnimalService() {
        return new Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://api.petfinder.com/")
                .build()
                .create(AnimalService.class);
    }

    @Provides
    @IOScheduler
    Scheduler provideIOScheduler() {
        return Schedulers.io();
    }

    @Provides
    @Singleton
    @MainThreadScheduler
    Scheduler provideMainScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
