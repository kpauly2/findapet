package tech.pauly.findapet.discover;

import android.databinding.ObservableInt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.shared.ActivityEvent;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.ViewEventBus;

public class DiscoverViewModel extends BaseViewModel {

    public ObservableInt columnCount = new ObservableInt(2);

    private AnimalRepository animalRepository;
    private AnimalTypeViewPagerAdapter viewPagerAdapter;
    private AnimalListAdapter listAdapter;
    private AnimalListItemViewModel.Factory animalListItemFactory;

    @Inject
    public DiscoverViewModel(AnimalRepository animalRepository,
                             AnimalTypeViewPagerAdapter viewPagerAdapter,
                             AnimalListAdapter listAdapter,
                             AnimalListItemViewModel.Factory animalListItemFactory) {
        this.animalRepository = animalRepository;
        this.viewPagerAdapter = viewPagerAdapter;
        this.listAdapter = listAdapter;
        this.animalListItemFactory = animalListItemFactory;

        viewPagerAdapter.setViewModel(this);
    }

    public AnimalListAdapter getListAdapter() {
        return listAdapter;
    }

    public AnimalTypeViewPagerAdapter getViewPagerAdapter() {
        return viewPagerAdapter;
    }

    public void fetchAnimalsForNewPage(int position) {
        listAdapter.clearItems();
        fetchAnimals(AnimalType.values()[position]);
    }

    void fetchAnimals(AnimalType animalType) {
        subscribeOnLifecycle(animalRepository.fetchAnimals(animalType)
                                             .subscribe(this::setupAnimalList, Throwable::printStackTrace));
    }

    private void setupAnimalList(AnimalListResponse animalListResponse) {
        List<AnimalListItemViewModel> viewModelList = new ArrayList<>();
        if (animalListResponse.getAnimalList() != null) {
            for (Animal animal : animalListResponse.getAnimalList()) {
                viewModelList.add(animalListItemFactory.newInstance(animal));
            }
        }
        listAdapter.setAnimalItems(viewModelList);
    }
}
