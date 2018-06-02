package tech.pauly.findapet.dependencyinjection;

import android.content.Context;
import android.support.test.espresso.idling.CountingIdlingResource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.AnimalService;
import tech.pauly.findapet.data.FakeAnimalRepository;
import tech.pauly.findapet.data.ObservableHelper;
import tech.pauly.findapet.shared.TestLocationHelper;

@Module
public class EspressoModule {

    @Provides
    @Singleton
    public CountingIdlingResource provideCountingIdlingResource() {
        return new CountingIdlingResource("all resources");
    }

    @Provides
    @Singleton
    public AnimalRepository provideFakeAnimalRepository(AnimalService animalService, ObservableHelper observableHelper, CountingIdlingResource countingIdlingResource) {
        return new FakeAnimalRepository(animalService, observableHelper, countingIdlingResource);
    }

    @Provides
    @Singleton
    public TestLocationHelper provideTestLocationHelper(@ForApplication Context context, CountingIdlingResource countingIdlingResource) {
        return new TestLocationHelper(context, countingIdlingResource);
    }
}
