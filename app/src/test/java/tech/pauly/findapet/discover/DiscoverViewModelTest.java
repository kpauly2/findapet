package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.Observable;
import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalSize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiscoverViewModelTest {

    @Mock
    private AnimalRepository animalRepository;
    private AnimalListResponse animalListResponse;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        animalListResponse = mock(AnimalListResponse.class);
        when(animalRepository.fetchAnimals()).thenReturn(Observable.just(animalListResponse));
    }

    @Test
    public void onCreate_fetchAnimals() {
        new DiscoverViewModel(animalRepository);

        verify(animalRepository).fetchAnimals();
    }

    @Test
    public void fetchAnimals_onNext_outputAnimalText() {
        Animal animal = mock(Animal.class);
        when(animal.getName()).thenReturn("name");
        when(animal.getAge()).thenReturn(Age.Adult);
        when(animal.getBreedList()).thenReturn(Collections.singletonList("breed"));
        when(animal.getSize()).thenReturn(AnimalSize.M);
        when(animal.getId()).thenReturn(1);
        when(animalListResponse.getAnimalList()).thenReturn(Collections.singletonList(animal));

        DiscoverViewModel subject = new DiscoverViewModel(animalRepository);

        assertThat(subject.tempOutput.get()).isEqualTo("ANIMALS:\nname\nAdult\nbreed\nM\n1\n\n");
    }
}