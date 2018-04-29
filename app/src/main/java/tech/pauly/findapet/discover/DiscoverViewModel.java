package tech.pauly.findapet.discover;

import javax.inject.Inject;

import tech.pauly.findapet.repository.AnimalRepository;

public class DiscoverViewModel {

    AnimalRepository animalRepository;

    @Inject
    public DiscoverViewModel(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
        fetchAnimals();
    }

    private void fetchAnimals() {
        animalRepository.fetchAnimals();
    }
}
