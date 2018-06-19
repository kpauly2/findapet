package tech.pauly.findapet.discover;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.location.Address;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import tech.pauly.findapet.R;
import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.PetfinderException;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.FetchAnimalsRequest;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.LocationHelper;
import tech.pauly.findapet.shared.PermissionHelper;
import tech.pauly.findapet.shared.ResourceProvider;
import tech.pauly.findapet.shared.datastore.DiscoverAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.PermissionEvent;
import tech.pauly.findapet.shared.events.PermissionListener;
import tech.pauly.findapet.shared.events.ViewEventBus;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DiscoverViewModel extends BaseViewModel {

    public ObservableInt columnCount = new ObservableInt(2);
    public ObservableBoolean refreshing = new ObservableBoolean(false);
    public ObservableBoolean locationMissing = new ObservableBoolean(false);
    public ObservableBoolean animalsMissing = new ObservableBoolean(false);
    public ObservableList<Chip> chipList = new ObservableArrayList<>();
    public ObservableField<Chip> locationChip = new ObservableField<>();

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
            eventBus.send(new PermissionEvent(this,
                                              new String[] { ACCESS_FINE_LOCATION },
                                              locationPermissionResponseListener(),
                                              100));
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

    public boolean getErrorVisible() {
        return locationMissing.get() || animalsMissing.get();
    }

    private void fetchAnimals() {
        refreshing.set(true);
        onLifecycle(Observable.zip(getCurrentLocation(),
                                   getCurrentFilter(),
                                   (location, filter) -> new FetchAnimalsRequest(animalType, lastOffset, location.getPostalCode(), filter))
                              .flatMap(animalRepository::fetchAnimals)
                              .subscribe(this::setAnimalList, this::showError));
    }

    private Observable<Filter> getCurrentFilter() {
        return filterRepository.getCurrentFilterAndNoFilterIfEmpty()
                               .doOnSubscribe(this::removeFilterChips)
                               .doOnSuccess(this::addFilterChips).toObservable();
    }

    private Observable<Address> getCurrentLocation() {
        return locationHelper.fetchCurrentLocation()
                             .doOnSubscribe(this::removeLocationChip)
                             .doOnNext(this::addLocationChip);
    }

    private void removeFilterChips(Disposable disposable) {
        chipList.clear();
    }

    private void addFilterChips(Filter filter) {
        if (filter.getSex() != Sex.MISSING) {
            chipList.add(new Chip(resourceProvider.getString(filter.getSex().getFormattedName())));
        }

        if (filter.getAge() != Age.MISSING) {
            chipList.add(new Chip(resourceProvider.getString(filter.getAge().getFormattedName())));
        }

        if (filter.getSize() != AnimalSize.MISSING) {
            chipList.add(new Chip(resourceProvider.getString(filter.getSize().getFormattedName())));
        }

        if (!filter.getBreed().equals("")) {
            chipList.add(new Chip(filter.getBreed()));
        }
    }

    private void removeLocationChip(Disposable disposable) {
        locationChip.set(null);
    }

    private void addLocationChip(Address location) {
        locationChip.set(new Chip(resourceProvider.getString(R.string.chip_near_location, location.getPostalCode())));
    }

    private void showError(Throwable throwable) {
        refreshing.set(false);
        if (throwable instanceof PetfinderException) {
            switch (((PetfinderException) throwable).getStatusCode()) {
                case ERR_NO_ANIMALS:
                    animalsMissing.set(true);
                    return;
            }
        }
        throwable.printStackTrace();
    }

    private void setAnimalList(AnimalListResponse animalListResponse) {
        refreshing.set(false);
        List<AnimalListItemViewModel> viewModelList = new ArrayList<>();
        animalsMissing.set(false);
        lastOffset = animalListResponse.getLastOffset();
        for (Animal animal : animalListResponse.getAnimalList()) {
            viewModelList.add(animalListItemFactory.newInstance(animal));
        }
        listAdapter.setAnimalItems(viewModelList);
    }

    private PermissionListener locationPermissionResponseListener() {
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
