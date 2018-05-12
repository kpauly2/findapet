package tech.pauly.findapet.discover;

import android.databinding.ObservableInt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.shared.BaseViewModel;

public class DiscoverViewModel extends BaseViewModel {
    public ObservableInt columnCount = new ObservableInt(2);

    private AnimalTypeViewPagerAdapter viewPagerAdapter;

    private List<AnimalTypeListViewModel> animalTypeListViewModels = new ArrayList<>();

    @Inject
    public DiscoverViewModel(AnimalTypeViewPagerAdapter viewPagerAdapter,
                             AnimalTypeListViewModel.Factory animalTypeListViewModelFactory) {
        this.viewPagerAdapter = viewPagerAdapter;

        viewPagerAdapter.setMainViewModel(this);
        viewPagerAdapter.setListViewModels(createAnimalTypeViewModels(animalTypeListViewModelFactory));
    }

    public AnimalTypeViewPagerAdapter getViewPagerAdapter() {
        return viewPagerAdapter;
    }

    public void onPageChange(int position) {
        animalTypeListViewModels.get(position).onPageChange();
    }

    private List<AnimalTypeListViewModel> createAnimalTypeViewModels(AnimalTypeListViewModel.Factory animalTypeListViewModelFactory) {
        animalTypeListViewModels.clear();
        for (AnimalType type : AnimalType.values()) {
            animalTypeListViewModels.add(animalTypeListViewModelFactory.newInstance(type));
        }
        return animalTypeListViewModels;
    }
}
