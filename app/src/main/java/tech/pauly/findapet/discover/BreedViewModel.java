package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.BreedRepository;
import tech.pauly.findapet.data.models.BreedListResponse;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.datastore.BreedAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

public class BreedViewModel extends BaseViewModel {

    public ObservableList<String> breedList = new ObservableArrayList<>();
    private BreedRepository breedRepository;
    private TransientDataStore dataStore;

    @Inject
    BreedViewModel(BreedRepository breedRepository, TransientDataStore dataStore) {
        this.breedRepository = breedRepository;
        this.dataStore = dataStore;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void updateBreedList() {
        BreedAnimalTypeUseCase useCase = dataStore.get(BreedAnimalTypeUseCase.class);
        if (useCase != null) {
            subscribeOnLifecycle(breedRepository.getBreedList(useCase.getAnimalType())
                                                .subscribe(this::populateBreedList, Throwable::printStackTrace));
        }
    }

    public void saveBreed(View v) {
        //TODO: https://www.pivotaltracker.com/story/show/157159549
    }

    private void populateBreedList(BreedListResponse response) {
        List<String> breedList = response.getBreedList();
        if (breedList != null) {
            this.breedList.addAll(breedList);
        }
    }
}
