package tech.pauly.findapet.shared.datastore;

import tech.pauly.findapet.data.models.AnimalType;

public class BreedAnimalTypeUseCase implements UseCase {
    private AnimalType animalType;

    public BreedAnimalTypeUseCase(AnimalType animalType) {
        this.animalType = animalType;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }
}
