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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiscoverViewModelTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private AnimalListAdapter listAdapter;

    @Mock
    private AnimalTypeViewPagerAdapter viewPagerAdapter;

    @Mock
    private AnimalListItemViewModel.Factory animalListItemFactory;

    @Mock
    private AnimalListItemViewModel animalListItemViewModel;

    private AnimalListResponse animalListResponse;
    private DiscoverViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        animalListResponse = mock(AnimalListResponse.class);
        when(animalRepository.fetchAnimals(any(AnimalType.class))).thenReturn(Observable.just(animalListResponse));
        when(animalListItemFactory.newInstance(any(Animal.class))).thenReturn(animalListItemViewModel);
        subject = new DiscoverViewModel(animalRepository, viewPagerAdapter, listAdapter, animalListItemFactory);
    }

    @Test
    public void fetchAnimals_onNext_sendAnimalListToAdapter() {
        Animal animal = mock(Animal.class);
        when(animalListResponse.getAnimalList()).thenReturn(Collections.singletonList(animal));
        ArgumentCaptor<List<AnimalListItemViewModel>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        subject.fetchAnimals(AnimalType.Cat);

        verify(listAdapter).setAnimalItems(argumentCaptor.capture());
        verify(animalListItemFactory).newInstance(animal);
    }

    @Test
    public void fetchAnimals_onNextAndAnimalListNull_setsEmptyList() {
        when(animalListResponse.getAnimalList()).thenReturn(null);
        ArgumentCaptor<List<AnimalListItemViewModel>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        subject.fetchAnimals(AnimalType.Cat);

        verify(listAdapter).setAnimalItems(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().size()).isEqualTo(0);
    }

    @Test
    public void fetchAnimalsForNewPage_clearsItemsAndFetchesAnimalForNewPage() {
        subject.fetchAnimalsForNewPage(0);

        verify(listAdapter).clearItems();
        assertThat(animalRepository.fetchAnimals(AnimalType.Dog));
    }
}