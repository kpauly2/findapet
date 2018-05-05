package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableInt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;

public class DiscoverViewModel implements LifecycleObserver {

    public ObservableInt columnCount = new ObservableInt(2);

    private AnimalRepository animalRepository;
    private AnimalListAdapter adapter;

    @Inject
    public DiscoverViewModel(AnimalRepository animalRepository, AnimalListAdapter animalListAdapter) {
        this.animalRepository = animalRepository;
        this.adapter = animalListAdapter;
    }

    public AnimalListAdapter getAdapter() {
        return adapter;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void fetchAnimals() {
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
