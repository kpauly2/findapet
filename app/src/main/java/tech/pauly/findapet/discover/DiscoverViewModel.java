package tech.pauly.findapet.discover;

import android.databinding.ObservableField;

import javax.inject.Inject;

import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;

public class DiscoverViewModel {

    public ObservableField<String> tempOutput = new ObservableField<>("waiting");

    private AnimalRepository animalRepository;

    @Inject
    public DiscoverViewModel(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
        fetchAnimals();
    }

    private void fetchAnimals() {
        animalRepository.fetchAnimals().subscribe(animalListResponse -> {
            StringBuilder outputString = new StringBuilder("ANIMALS:\n");
            for (Animal animal : animalListResponse.getAnimalList()) {
                outputString.append(animal.getName()).append("\n")
                            .append(animal.getAge()).append("\n");
                if (animal.getBreedList().size() > 0) {
                    outputString.append(animal.getBreedList().get(0)).append("\n");
                }
                outputString.append(animal.getSize()).append("\n")
                            .append(animal.getId()).append("\n\n");
            }
            tempOutput.set(outputString.toString());
        }, Throwable::printStackTrace);
    }
}
