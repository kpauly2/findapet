package tech.pauly.findapet.data;

import javax.inject.Inject;

import io.reactivex.Observable;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;

public class AnimalRepository {

    private AnimalService animalService;
    private ObservableHelper observableHelper;

    @Inject
    public AnimalRepository(AnimalService animalService, ObservableHelper observableHelper) {
        this.animalService = animalService;
        this.observableHelper = observableHelper;
    }

    public Observable<AnimalListResponse> fetchAnimals(AnimalType animalType) {
        //TODO: remove hardcoded values in https://www.pivotaltracker.com/story/show/157157373 and https://www.pivotaltracker.com/story/show/157179681
        return animalService.fetchAnimals("48335", "23bfea78dfb8bde56bbae16192cbe6d4", animalType.name().toLowerCase())
                            .compose(observableHelper.applySchedulers());
    }
}

