package tech.pauly.findapet.data;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import tech.pauly.findapet.BuildConfig;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.FetchAnimalsRequest;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.StatusCode;

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
                                          filter.getSize().getServerName(),
                                          filter.getBreed())
                            .flatMap(animalListResponse -> {
                                if (animalListResponse.getAnimalList() == null
                                    || animalListResponse.getAnimalList().size() == 0) {
                                    return Single.error(new PetfinderException(StatusCode.ERR_NO_ANIMALS));
                                }
                                return Single.just(animalListResponse);
                            })
                            .compose(observableHelper.applySingleSchedulers())
                            .toObservable();
    }
}

