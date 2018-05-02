package tech.pauly.findapet.discover;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;

public class DiscoverViewModel {

    private AnimalRepository animalRepository;
    private AnimalListAdapter adapter;

    @Inject
    public DiscoverViewModel(AnimalRepository animalRepository, AnimalListAdapter animalListAdapter) {
        this.animalRepository = animalRepository;
        this.adapter = animalListAdapter;

        fetchAnimals();
    }

    public AnimalListAdapter getAdapter() {
        return adapter;
    }

    private void fetchAnimals() {
        animalRepository.fetchAnimals().subscribe(this::setupAnimalList, Throwable::printStackTrace);
    }

    private void setupAnimalList(AnimalListResponse animalListResponse) {
        List<AnimalListItemViewModel> viewModelList = new ArrayList<>();
        for (Animal animal : animalListResponse.getAnimalList()) {
            viewModelList.add(new AnimalListItemViewModel(animal));
        }
        adapter.setAnimalItems(viewModelList);
    }
}
