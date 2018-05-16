package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableInt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

public class DiscoverViewModel extends BaseViewModel {
    public ObservableInt columnCount = new ObservableInt(2);

    private final AnimalListAdapter listAdapter;
    private final AnimalListItemViewModel.Factory animalListItemFactory;
    private final AnimalRepository animalRepository;
    private TransientDataStore dataStore;

    private AnimalType animalType = AnimalType.CAT;
    private int lastOffset = 0;

    @Inject
    public DiscoverViewModel(AnimalListAdapter listAdapter,
                             AnimalListItemViewModel.Factory animalListItemFactory,
                             AnimalRepository animalRepository,
                             TransientDataStore dataStore) {
        this.listAdapter = listAdapter;
        this.animalListItemFactory = animalListItemFactory;
        this.animalRepository = animalRepository;
        this.dataStore = dataStore;
    }

    public AnimalListAdapter getListAdapter() {
        return listAdapter;
    }

    public void reloadWithNewAnimalType(AnimalType animalType) {
        this.animalType = animalType;
        reloadList();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void reloadList() {
        lastOffset = 0;
        dataStore.save(new DiscoverToolbarTitleUseCase(animalType.getToolbarName()));
        fetchAnimals();
    }

    public void loadMoreAnimals() {
        fetchAnimals();
    }

    private void fetchAnimals() {
        subscribeOnLifecycle(animalRepository.fetchAnimals(animalType, lastOffset)
                                             .subscribe(this::setAnimalList, Throwable::printStackTrace));
    }

    private void setAnimalList(AnimalListResponse animalListResponse) {
        List<AnimalListItemViewModel> viewModelList = new ArrayList<>();
        if (animalListResponse.getAnimalList() != null) {
            lastOffset = animalListResponse.getLastOffset();
            for (Animal animal : animalListResponse.getAnimalList()) {
                viewModelList.add(animalListItemFactory.newInstance(animal));
            }
        }
        listAdapter.setAnimalItems(viewModelList);
    }
}
