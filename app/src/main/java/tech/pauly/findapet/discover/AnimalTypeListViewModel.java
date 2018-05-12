package tech.pauly.findapet.discover;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.shared.BaseViewModel;

public class AnimalTypeListViewModel extends BaseViewModel {
    private AnimalListAdapter listAdapter;
    private AnimalListItemViewModel.Factory animalListItemFactory;
    private AnimalRepository animalRepository;

    private AnimalType animalType;
    private boolean animalsLoaded = false;
    private int lastOffset = 0;

    protected AnimalTypeListViewModel(AnimalType animalType,
                                      AnimalListAdapter listAdapter,
                                      AnimalListItemViewModel.Factory animalListItemFactory,
                                      AnimalRepository animalRepository) {
        this.animalType = animalType;
        this.listAdapter = listAdapter;
        this.animalListItemFactory = animalListItemFactory;
        this.animalRepository = animalRepository;
    }

    public AnimalListAdapter getListAdapter() {
        return listAdapter;
    }

    public void fetchAnimals() {
        subscribeOnLifecycle(animalRepository.fetchAnimals(animalType, lastOffset)
                                             .subscribe(this::setAnimalList, Throwable::printStackTrace));
    }

    public void onPageChange() {
        if (!animalsLoaded) {
            fetchAnimals();
        }
    }

    public void loadMoreAnimals() {
        fetchAnimals();
    }

    private void setAnimalList(AnimalListResponse animalListResponse) {
        List<AnimalListItemViewModel> viewModelList = new ArrayList<>();
        if (animalListResponse.getAnimalList() != null) {
            animalsLoaded = true;
            lastOffset = animalListResponse.getLastOffset();
            for (Animal animal : animalListResponse.getAnimalList()) {
                viewModelList.add(animalListItemFactory.newInstance(animal));
            }
        }
        listAdapter.setAnimalItems(viewModelList);
    }

    public static class Factory {
        private AnimalListItemViewModel.Factory animalListItemFactory;
        private AnimalRepository animalRepository;

        @Inject
        public Factory(AnimalListItemViewModel.Factory animalListItemFactory,
                       AnimalRepository animalRepository) {
            this.animalListItemFactory = animalListItemFactory;
            this.animalRepository = animalRepository;
        }

        public AnimalTypeListViewModel newInstance(AnimalType animalType) {
            return new AnimalTypeListViewModel(animalType, new AnimalListAdapter(), animalListItemFactory, animalRepository);
        }
    }
}
