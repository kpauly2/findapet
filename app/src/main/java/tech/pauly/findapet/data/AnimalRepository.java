package tech.pauly.findapet.data;

import javax.inject.Inject;

import io.reactivex.Observable;
import tech.pauly.findapet.data.models.AnimalListResponse;

public class AnimalRepository {

    private AnimalService animalService;
    private ObservableHelper observableHelper;

    @Inject
    AnimalRepository(AnimalService animalService, ObservableHelper observableHelper) {
        this.animalService = animalService;
        this.observableHelper = observableHelper;
    }

    public Observable<AnimalListResponse> fetchAnimals() {
        return animalService.fetchAnimals("48335", "23bfea78dfb8bde56bbae16192cbe6d4")
                            .compose(observableHelper.applySchedulers());
    }
}
