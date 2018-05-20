package tech.pauly.findapet.shared.datastore;

import java.util.Objects;

import tech.pauly.findapet.data.models.AnimalType;

public class DiscoverAnimalTypeUseCase implements UseCase {

    private AnimalType animalType;

    public DiscoverAnimalTypeUseCase(AnimalType animalType) {
        this.animalType = animalType;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DiscoverAnimalTypeUseCase that = (DiscoverAnimalTypeUseCase) o;
        return animalType == that.animalType;
    }

    @Override
    public int hashCode() {

        return Objects.hash(animalType);
    }
}
