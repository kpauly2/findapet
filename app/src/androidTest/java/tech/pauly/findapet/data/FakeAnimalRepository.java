package tech.pauly.findapet.data;

import android.support.test.espresso.idling.CountingIdlingResource;

import io.reactivex.Observable;
import tech.pauly.findapet.data.models.AnimalListResponse;

public class FakeAnimalRepository extends AnimalRepository {

    private CountingIdlingResource countingIdlingResource;

    public FakeAnimalRepository(AnimalService animalService, ObservableHelper observableHelper, CountingIdlingResource countingIdlingResource) {
        super(animalService, observableHelper);
        this.countingIdlingResource = countingIdlingResource;
    }

    @Override
    public Observable<AnimalListResponse> fetchAnimals() {
        countingIdlingResource.increment();
        return super.fetchAnimals().doAfterTerminate(() -> countingIdlingResource.decrement());
    }
}
