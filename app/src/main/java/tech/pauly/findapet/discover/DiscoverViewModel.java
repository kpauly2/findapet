package tech.pauly.findapet.discover;

import android.arch.lifecycle.LifecycleObserver;
import android.databinding.ObservableInt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;

public class DiscoverViewModel implements LifecycleObserver {

    public ObservableInt columnCount = new ObservableInt(2);

    private AnimalRepository animalRepository;
    private AnimalTypeViewPagerAdapter viewPagerAdapter;
    private AnimalListAdapter listAdapter;

    @Inject
    public DiscoverViewModel(AnimalRepository animalRepository, AnimalTypeViewPagerAdapter viewPagerAdapter, AnimalListAdapter animalListAdapter) {
        this.animalRepository = animalRepository;
        this.viewPagerAdapter = viewPagerAdapter;
        this.listAdapter = animalListAdapter;

        viewPagerAdapter.setViewModel(this);
    }

    public AnimalListAdapter getListAdapter() {
        return listAdapter;
    }

    public AnimalTypeViewPagerAdapter getViewPagerAdapter() {
        return viewPagerAdapter;
    }

    public void pageChange(int position) {
        listAdapter.clearItems();
        fetchAnimals(AnimalType.values()[position]);
    }

    void fetchAnimals(AnimalType animalType) {
        animalRepository.fetchAnimals(animalType).subscribe(this::setupAnimalList, Throwable::printStackTrace);
    }

    private void setupAnimalList(AnimalListResponse animalListResponse) {
        List<AnimalListItemViewModel> viewModelList = new ArrayList<>();
        for (Animal animal : animalListResponse.getAnimalList()) {
            viewModelList.add(new AnimalListItemViewModel(animal));
        }
        listAdapter.setAnimalItems(viewModelList);
    }
}
