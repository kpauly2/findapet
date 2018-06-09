package tech.pauly.findapet.shared.datastore;

import tech.pauly.findapet.data.models.AnimalType;

public class FilterAnimalTypeUseCase implements UseCase {
    private AnimalType animalType;

    public FilterAnimalTypeUseCase(AnimalType animalType) {
        this.animalType = animalType;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }
}
