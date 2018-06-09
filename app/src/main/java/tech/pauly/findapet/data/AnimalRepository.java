package tech.pauly.findapet.data;

import javax.inject.Inject;

import io.reactivex.Observable;
import tech.pauly.findapet.BuildConfig;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.FetchAnimalsRequest;
import tech.pauly.findapet.data.models.Filter;

public class AnimalRepository {

    private static final int ANIMAL_RETURN_COUNT = 20;

    private AnimalService animalService;
    private ObservableHelper observableHelper;

    @Inject
    public AnimalRepository(AnimalService animalService, ObservableHelper observableHelper) {
        this.animalService = animalService;
        this.observableHelper = observableHelper;
    }

    public Observable<AnimalListResponse> fetchAnimals(FetchAnimalsRequest request) {
        Filter filter = request.getFilter();
        return animalService.fetchAnimals(request.getLocation(),
                                          BuildConfig.API_KEY,
                                          request.getAnimalType().getServerName(),
                                          request.getLastOffset(),
                                          ANIMAL_RETURN_COUNT,
                                          filter.getSex().getServerName(),
                                          filter.getAge().getServerName(),
                                          filter.getSize().getServerName())
                            .compose(observableHelper.applySingleSchedulers())
                            .toObservable();
    }
}

