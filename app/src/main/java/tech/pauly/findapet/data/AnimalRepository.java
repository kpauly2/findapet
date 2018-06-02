package tech.pauly.findapet.data;

import javax.inject.Inject;

import io.reactivex.Single;
import tech.pauly.findapet.BuildConfig;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;

public class AnimalRepository {

    private static final int ANIMAL_RETURN_COUNT = 20;

    private AnimalService animalService;
    private ObservableHelper observableHelper;

    @Inject
    public AnimalRepository(AnimalService animalService, ObservableHelper observableHelper) {
        this.animalService = animalService;
        this.observableHelper = observableHelper;
    }

    public Single<AnimalListResponse> fetchAnimals(String location, AnimalType animalType, int offset) {
        return animalService.fetchAnimals(location, BuildConfig.API_KEY, animalType.name().toLowerCase(), offset, ANIMAL_RETURN_COUNT)
                            .compose(observableHelper.applySchedulers());
    }
}

