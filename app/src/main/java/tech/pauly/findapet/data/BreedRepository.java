package tech.pauly.findapet.data;

import javax.inject.Inject;

import io.reactivex.Single;
import tech.pauly.findapet.BuildConfig;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.BreedListResponse;

public class BreedRepository {

    private BreedService breedService;
    private ObservableHelper observableHelper;

    @Inject
    BreedRepository(BreedService breedService, ObservableHelper observableHelper) {
        this.breedService = breedService;
        this.observableHelper = observableHelper;
    }

    public Single<BreedListResponse> getBreedList(AnimalType animalType) {
        return breedService.fetchBreeds(BuildConfig.API_KEY, animalType.getServerName())
                           .compose(observableHelper.applySingleSchedulers());
    }
}
