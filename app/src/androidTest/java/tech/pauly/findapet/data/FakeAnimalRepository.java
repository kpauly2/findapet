package tech.pauly.findapet.data;

import android.support.test.espresso.idling.CountingIdlingResource;

import io.reactivex.Single;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;

public class FakeAnimalRepository extends AnimalRepository {

    private CountingIdlingResource countingIdlingResource;

    public FakeAnimalRepository(AnimalService animalService, ObservableHelper observableHelper, CountingIdlingResource countingIdlingResource) {
        super(animalService, observableHelper);
        this.countingIdlingResource = countingIdlingResource;
    }

    @Override
    public Single<AnimalListResponse> fetchAnimals(String location, AnimalType animalType, int offset) {
        countingIdlingResource.increment();
        return super.fetchAnimals(location, animalType, offset).doAfterTerminate(() -> countingIdlingResource.decrement());
    }
}
