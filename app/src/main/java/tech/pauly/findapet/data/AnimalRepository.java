package tech.pauly.findapet.data;

import javax.inject.Inject;

import io.reactivex.Single;
import tech.pauly.findapet.BuildConfig;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.FetchAnimalsRequest;

public class AnimalRepository {

    private static final int ANIMAL_RETURN_COUNT = 20;

    private AnimalService animalService;
    private ObservableHelper observableHelper;

    @Inject
    public AnimalRepository(AnimalService animalService, ObservableHelper observableHelper) {
        this.animalService = animalService;
        this.observableHelper = observableHelper;
    }

    public Single<AnimalListResponse> fetchAnimals(FetchAnimalsRequest request) {
        return animalService.fetchAnimals(request.getLocation(),
                                          BuildConfig.API_KEY,
                                          request.getAnimalType().name().toLowerCase(),
                                          request.getLastOffset(),
                                          ANIMAL_RETURN_COUNT,
                                          request.getFilter().getSex().getServerName())
                            .compose(observableHelper.applySchedulers());
    }
}

