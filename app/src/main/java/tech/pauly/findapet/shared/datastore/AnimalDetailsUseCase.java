package tech.pauly.findapet.shared.datastore;

import tech.pauly.findapet.data.models.Animal;

public class AnimalDetailsUseCase implements UseCase {
    private Animal animal;

    public AnimalDetailsUseCase(Animal animal) {
        this.animal = animal;
    }

    public Animal getAnimal() {
        return animal;
    }
}
