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
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.Media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiscoverViewModelTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private AnimalListAdapter animalListAdapter;

    private AnimalListResponse animalListResponse;
    private DiscoverViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        animalListResponse = mock(AnimalListResponse.class);
        when(animalRepository.fetchAnimals()).thenReturn(Observable.just(animalListResponse));
        subject = new DiscoverViewModel(animalRepository, animalListAdapter);
    }

    @Test
    public void onResume_fetchAnimals() {
        subject.fetchAnimals();

        verify(animalRepository).fetchAnimals();
    }

    @Test
    public void fetchAnimals_onNext_sendAnimalListToAdapter() {
        Animal animal = mock(Animal.class);
        when(animal.getName()).thenReturn("name");
        when(animal.getAge()).thenReturn(Age.Adult);
        when(animal.getBreedList()).thenReturn(Collections.singletonList("breed"));
        when(animal.getSize()).thenReturn(AnimalSize.M);
        when(animal.getId()).thenReturn(1);
        Media media = mock(Media.class);
        when(animal.getMedia()).thenReturn(media);
        when(animalListResponse.getAnimalList()).thenReturn(Collections.singletonList(animal));
        ArgumentCaptor<List<AnimalListItemViewModel>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        subject.fetchAnimals();

        verify(animalListAdapter).setAnimalItems(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().get(0).name.get()).isEqualTo("name");
    }
}