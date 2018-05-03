package tech.pauly.findapet.dependencyinjection;

import android.support.test.espresso.idling.CountingIdlingResource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.AnimalService;
import tech.pauly.findapet.data.FakeAnimalRepository;
import tech.pauly.findapet.data.ObservableHelper;

@Module
public class EspressoModule {

    @Provides
    @Singleton
    public CountingIdlingResource provideCountingIdlingResource() {
        return new CountingIdlingResource("all resources");
    }

    @Provides
    public AnimalRepository provideFakeAnimalRepository(AnimalService animalService, ObservableHelper observableHelper, CountingIdlingResource countingIdlingResource) {
        return new FakeAnimalRepository(animalService, observableHelper, countingIdlingResource);
    }
}
