package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import tech.pauly.findapet.R;
import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.FetchAnimalsRequest;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.ResourceProvider;
import tech.pauly.findapet.shared.LocationHelper;
import tech.pauly.findapet.shared.PermissionHelper;
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase;
import tech.pauly.findapet.shared.events.PermissionEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;
import tech.pauly.findapet.shared.datastore.DiscoverAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static tech.pauly.findapet.discover.Chip.Type.FILTER;
import static tech.pauly.findapet.discover.Chip.Type.LOCATION;

public class DiscoverViewModel extends BaseViewModel {

    public ObservableInt columnCount = new ObservableInt(2);
    public ObservableBoolean refreshing = new ObservableBoolean(false);
    public ObservableBoolean locationMissing = new ObservableBoolean(false);
    public ObservableList<Chip> chipList = new ObservableArrayList<>();

    private final AnimalListAdapter listAdapter;
    private final AnimalListItemViewModel.Factory animalListItemFactory;
    private final AnimalRepository animalRepository;
    private TransientDataStore dataStore;
    private PermissionHelper permissionHelper;
    private ViewEventBus eventBus;
    private LocationHelper locationHelper;
    private ResourceProvider resourceProvider;
    private FilterRepository filterRepository;

    private AnimalType animalType = AnimalType.CAT;
    private int lastOffset = 0;
    private boolean firstLoad = true;

    @Inject
    public DiscoverViewModel(AnimalListAdapter listAdapter,
                             AnimalListItemViewModel.Factory animalListItemFactory,
                             AnimalRepository animalRepository,
                             TransientDataStore dataStore,
                             PermissionHelper permissionHelper,
                             ViewEventBus eventBus,
                             LocationHelper locationHelper,
                             ResourceProvider resourceProvider,
                             FilterRepository filterRepository) {
        this.listAdapter = listAdapter;
        this.animalListItemFactory = animalListItemFactory;
        this.animalRepository = animalRepository;
        this.dataStore = dataStore;
        this.permissionHelper = permissionHelper;
        this.eventBus = eventBus;
        this.locationHelper = locationHelper;
        this.resourceProvider = resourceProvider;
        this.filterRepository = filterRepository;

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
        FilterUpdatedUseCase useCase = dataStore.get(FilterUpdatedUseCase.class);
        if (firstLoad || useCase != null) {
            firstLoad = false;
            requestPermissionToLoad();
        }
    }

    public void requestPermissionToLoad() {
        if (permissionHelper.hasPermissions(ACCESS_FINE_LOCATION)) {
            loadList();
        } else {
            eventBus.send(PermissionEvent.build(this)
                                         .requestPermissions(ACCESS_FINE_LOCATION)
                                         .listener(locationPermissionResponseListener())
                                         .code(100));
        }
    }

    private void loadList() {
        dataStore.save(new DiscoverToolbarTitleUseCase(animalType.getToolbarName()));
        listAdapter.clearAnimalItems();
        lastOffset = 0;
        fetchAnimals(true);
    }

    public void loadMoreAnimals() {
        fetchAnimals(false);
    }

    private void fetchAnimals(boolean resetLocation) {
        refreshing.set(true);
        subscribeOnLifecycle(Observable.zip(getCurrentLocation(resetLocation), getCurrentFilter(),
                                            (location, filter) -> new FetchAnimalsRequest(animalType, lastOffset, location, filter))
                                   .flatMap(animalRepository::fetchAnimals)
                                   .subscribe(this::setAnimalList, this::showError));
    }

    private Observable<Filter> getCurrentFilter() {
        return filterRepository.getCurrentFilterAndNoFilterIfEmpty()
                               .doOnSuccess(this::addFilterChips).toObservable();
    }

    private Observable<String> getCurrentLocation(boolean resetLocation) {
        return locationHelper.getCurrentLocation(resetLocation)
                     .doOnNext(this::addLocationChip);
    }

    private void addFilterChips(Filter filter) {
        for (Chip chip : chipList) {
            if (chip.getType() == FILTER) {
                chipList.remove(chip);
            }
        }
        if (filter.getSex() != Sex.U) {
            chipList.add(new Chip(FILTER, resourceProvider.getString(filter.getSex().getFormattedName())));
        }
    }

    private void addLocationChip(String location) {
        if (chipList.size() > 0 && chipList.get(0).getType() == LOCATION) {
            chipList.remove(0);
        }
        chipList.add(0, new Chip(LOCATION, resourceProvider.getString(R.string.chip_near_location, location)));
    }

    private void showError(Throwable throwable) {
        throwable.printStackTrace();
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
