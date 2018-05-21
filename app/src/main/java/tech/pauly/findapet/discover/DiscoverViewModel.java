package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.ContextProvider;
import tech.pauly.findapet.shared.events.PermissionEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;
import tech.pauly.findapet.shared.datastore.DiscoverAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DiscoverViewModel extends BaseViewModel {

    public ObservableInt columnCount = new ObservableInt(2);
    public ObservableBoolean refreshing = new ObservableBoolean(false);
    public ObservableBoolean locationMissing = new ObservableBoolean(false);

    private final AnimalListAdapter listAdapter;
    private final AnimalListItemViewModel.Factory animalListItemFactory;
    private final AnimalRepository animalRepository;
    private TransientDataStore dataStore;
    private ContextProvider contextProvider;
    private ViewEventBus eventBus;

    private AnimalType animalType = AnimalType.CAT;
    private int lastOffset = 0;
    private boolean firstLoad = true;

    @Inject
    public DiscoverViewModel(AnimalListAdapter listAdapter,
                             AnimalListItemViewModel.Factory animalListItemFactory,
                             AnimalRepository animalRepository,
                             TransientDataStore dataStore,
                             ContextProvider contextProvider,
                             ViewEventBus eventBus) {
        this.listAdapter = listAdapter;
        this.animalListItemFactory = animalListItemFactory;
        this.animalRepository = animalRepository;
        this.dataStore = dataStore;
        this.contextProvider = contextProvider;
        this.eventBus = eventBus;

        DiscoverAnimalTypeUseCase useCase = dataStore.get(DiscoverAnimalTypeUseCase.class);
        if (useCase != null) {
            animalType = useCase.getAnimalType();
        }
    }

    public AnimalListAdapter getListAdapter() {
        return listAdapter;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (firstLoad) {
            firstLoad = false;
            requestPermissionToLoad();
        }
    }

    public void requestPermissionToLoad() {
        if (contextProvider.hasPermission(ACCESS_FINE_LOCATION)) {
            loadList();
        } else {
            eventBus.send(PermissionEvent.build(this)
                                         .requestPermission(ACCESS_FINE_LOCATION)
                                         .listener(locationPermissionResponseListener())
                                         .code(100));
        }
    }

    private void loadList() {
        dataStore.save(new DiscoverToolbarTitleUseCase(animalType.getToolbarName()));
        listAdapter.clearAnimalItems();
        lastOffset = 0;
        fetchAnimals();
    }

    public void loadMoreAnimals() {
        fetchAnimals();
    }

    private void fetchAnimals() {
        refreshing.set(true);
        subscribeOnLifecycle(animalRepository.fetchAnimals(animalType, lastOffset)
                                             .subscribe(this::setAnimalList, Throwable::printStackTrace));
    }

    private void setAnimalList(AnimalListResponse animalListResponse) {
        refreshing.set(false);
        List<AnimalListItemViewModel> viewModelList = new ArrayList<>();
        if (animalListResponse.getAnimalList() != null) {
            lastOffset = animalListResponse.getLastOffset();
            for (Animal animal : animalListResponse.getAnimalList()) {
                viewModelList.add(animalListItemFactory.newInstance(animal));
            }
        }
        listAdapter.setAnimalItems(viewModelList);
    }

    private PermissionEvent.PermissionListener locationPermissionResponseListener() {
        return response -> {
            if (response.getPermission().equals(ACCESS_FINE_LOCATION)) {
                if (response.isGranted()) {
                    locationMissing.set(false);
                    requestPermissionToLoad();
                } else {
                    locationMissing.set(true);
                }
            }
        };
    }
}
