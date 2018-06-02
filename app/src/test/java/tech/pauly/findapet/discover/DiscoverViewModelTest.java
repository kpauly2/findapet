package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import tech.pauly.findapet.R;
import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.shared.ContextProvider;
import tech.pauly.findapet.shared.LocationHelper;
import tech.pauly.findapet.shared.PermissionHelper;
import tech.pauly.findapet.shared.datastore.DiscoverAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.PermissionEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiscoverViewModelTest {

    @Mock
    private AnimalListAdapter listAdapter;

    @Mock
    private AnimalListItemViewModel.Factory animalListItemFactory;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private TransientDataStore dataStore;

    @Mock
    private PermissionHelper permissionHelper;

    @Mock
    private ViewEventBus eventBus;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private AnimalListResponse animalListResponse;

    @Mock
    private ContextProvider contextProvider;

    private DiscoverViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DiscoverAnimalTypeUseCase useCase = mock(DiscoverAnimalTypeUseCase.class);
        when(useCase.getAnimalType()).thenReturn(AnimalType.CAT);
        when(dataStore.get(DiscoverAnimalTypeUseCase.class)).thenReturn(useCase);
        when(permissionHelper.hasPermissions(ACCESS_FINE_LOCATION)).thenReturn(true);
        when(animalRepository.fetchAnimals(anyString(), any(AnimalType.class), anyInt())).thenReturn(Observable.just(animalListResponse));
        when(locationHelper.getCurrentLocation()).thenReturn(Observable.just("zipcode"));
        when(contextProvider.getString(R.string.near_location, "zipcode")).thenReturn("Near zipcode");
        when(contextProvider.getString(R.string.near_location, "zipcode2")).thenReturn("Near zipcode2");
        subject = new DiscoverViewModel(listAdapter, animalListItemFactory, animalRepository, dataStore, permissionHelper, eventBus, locationHelper, contextProvider);
    }

    @Test
    public void onResume_firstLoad_loadListForCurrentLocationAndAnimalAndNoOffset() {
        subject.onResume();

        verify(animalRepository).fetchAnimals("zipcode", AnimalType.CAT, 0);
    }

    @Test
    public void onResume_notFirstLoad_doNothing() {
        subject.onResume();
        clearInvocations(animalRepository);
        subject.onResume();

        verify(animalRepository, never()).fetchAnimals(anyString(), any(AnimalType.class), anyInt());
    }

    @Test
    public void requestPermissionToLoad_locationPermissionNotGranted_requestPermission() {
        when(permissionHelper.hasPermissions(ACCESS_FINE_LOCATION)).thenReturn(false);
        ArgumentCaptor<PermissionEvent> argumentCaptor = ArgumentCaptor.forClass(PermissionEvent.class);

        subject.requestPermissionToLoad();

        verify(eventBus).send(argumentCaptor.capture());
        PermissionEvent sentEvent = argumentCaptor.getValue();
        assertThat(sentEvent.getRequestCode()).isEqualTo(100);
        assertThat(sentEvent.getPermissions()[0]).isEqualTo(ACCESS_FINE_LOCATION);
    }

    @Test
    public void requestPermissionToLoad_locationPermissionGranted_usesAnimalTypeFromDataStoreAndClearsListAndStartsRefreshingAndSetsLocationChip() {
        when(animalRepository.fetchAnimals(anyString(), any(AnimalType.class), anyInt())).thenReturn(Observable.empty());
        subject.requestPermissionToLoad();

        verify(dataStore).save(new DiscoverToolbarTitleUseCase(AnimalType.CAT.getToolbarName()));
        verify(animalRepository).fetchAnimals("zipcode", AnimalType.CAT, 0);
        verify(listAdapter).clearAnimalItems();
        assertThat(subject.refreshing.get()).isTrue();
        assertThat(subject.chipList.size()).isEqualTo(1);
        assertThat(subject.chipList.get(0)).isEqualTo("Near zipcode");
    }

    @Test
    public void requestPermissionToLoad_locationPermissionGrantedAndSearchASecondTime_resetsLocationChip() {
        when(animalRepository.fetchAnimals(anyString(), any(AnimalType.class), anyInt())).thenReturn(Observable.empty());
        subject.requestPermissionToLoad();
        when(locationHelper.getCurrentLocation()).thenReturn(Observable.just("zipcode2"));

        subject.requestPermissionToLoad();

        assertThat(subject.chipList.size()).isEqualTo(1);
        assertThat(subject.chipList.get(0)).isEqualTo("Near zipcode2");
    }

    @Test
    public void requestPermissionToLoad_fetchAnimalsOnNext_sendAnimalListToAdapterAndStopsRefreshing() {
        Animal animal = mock(Animal.class);
        when(animalListResponse.getAnimalList()).thenReturn(Collections.singletonList(animal));
        ArgumentCaptor<List<AnimalListItemViewModel>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        subject.requestPermissionToLoad();

        verify(listAdapter).setAnimalItems(argumentCaptor.capture());
        verify(animalListItemFactory).newInstance(animal);
        assertThat(subject.refreshing.get()).isFalse();
    }

    @Test
    public void requestPermissionToLoad_fetchAnimalsOnNextAndAnimalListNull_setsEmptyList() {
        when(animalListResponse.getAnimalList()).thenReturn(null);
        ArgumentCaptor<List<AnimalListItemViewModel>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        subject.requestPermissionToLoad();

        verify(listAdapter).setAnimalItems(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().size()).isEqualTo(0);
    }

    @Test
    public void loadMoreAnimals_fetchAnimalsAtCurrentOffset() {
        Animal animal = mock(Animal.class);
        when(animalListResponse.getLastOffset()).thenReturn(10);
        when(animalListResponse.getAnimalList()).thenReturn(Collections.singletonList(animal));
        subject.requestPermissionToLoad();
        verify(animalRepository).fetchAnimals("zipcode", AnimalType.CAT, 0);
        clearInvocations(animalRepository);

        subject.loadMoreAnimals();

        verify(animalRepository).fetchAnimals("zipcode", AnimalType.CAT, 10);
    }
}