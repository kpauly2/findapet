package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnimalTypeListViewModelTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private AnimalListAdapter listAdapter;

    @Mock
    private AnimalListItemViewModel.Factory animalListItemFactory;

    @Mock
    private AnimalListItemViewModel animalListItemViewModel;

    private AnimalListResponse animalListResponse;
    private AnimalTypeListViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        animalListResponse = mock(AnimalListResponse.class);
        when(animalRepository.fetchAnimals(any(AnimalType.class))).thenReturn(Observable.just(animalListResponse));
        when(animalListItemFactory.newInstance(any(Animal.class))).thenReturn(animalListItemViewModel);
        subject = new AnimalTypeListViewModel(AnimalType.Cat, listAdapter, animalListItemFactory, animalRepository);
    }

    @Test
    public void fetchAnimals_onNext_sendAnimalListToAdapter() {
        Animal animal = mock(Animal.class);
        when(animalListResponse.getAnimalList()).thenReturn(Collections.singletonList(animal));
        ArgumentCaptor<List<AnimalListItemViewModel>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        subject.fetchAnimals();

        verify(listAdapter).setAnimalItems(argumentCaptor.capture());
        verify(animalListItemFactory).newInstance(animal);
    }

    @Test
    public void fetchAnimals_onNextAndAnimalListNull_setsEmptyList() {
        when(animalListResponse.getAnimalList()).thenReturn(null);
        ArgumentCaptor<List<AnimalListItemViewModel>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        subject.fetchAnimals();

        verify(listAdapter).setAnimalItems(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().size()).isEqualTo(0);
    }

    @Test
    public void onPageChange_animalsHaveBeenLoaded_doNothing() {
        Animal animal = mock(Animal.class);
        when(animalListResponse.getAnimalList()).thenReturn(Collections.singletonList(animal));
        subject.fetchAnimals();
        clearInvocations(animalRepository);

        subject.onPageChange();

        verify(animalRepository, never()).fetchAnimals(any(AnimalType.class));
    }

    @Test
    public void onPageChange_animalsHaveNotBeenLoaded_fetchAnimals() {
        subject.onPageChange();

        verify(animalRepository).fetchAnimals(any(AnimalType.class));
    }

    @Test
    public void onPageChange_nullAnimalListFetched_fetchAnimals() {
        when(animalListResponse.getAnimalList()).thenReturn(null);
        subject.fetchAnimals();
        clearInvocations(animalRepository);

        subject.onPageChange();

        verify(animalRepository).fetchAnimals(any(AnimalType.class));
    }
}