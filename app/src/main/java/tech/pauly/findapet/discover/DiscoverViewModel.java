package tech.pauly.findapet.discover;

import android.databinding.ObservableField;

import javax.inject.Inject;

import tech.pauly.findapet.data.AnimalRepository;

public class DiscoverViewModel {

    public ObservableField<String> tempOutput = new ObservableField<>("waiting");

    private AnimalRepository animalRepository;

    @Inject
    public DiscoverViewModel(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
        fetchAnimals();
    }

    private void fetchAnimals() {
        animalRepository.fetchAnimals().subscribe(animalListResponse -> tempOutput.set("got it"), Throwable::printStackTrace);
    }
}
