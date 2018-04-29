package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Observable;
import tech.pauly.findapet.models.AnimalListResponse;
import tech.pauly.findapet.repository.AnimalRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiscoverViewModelTest {

    @Mock
    private AnimalRepository animalRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(animalRepository.fetchAnimals()).thenReturn(Observable.just(mock(AnimalListResponse.class)));
    }

    @Test
    public void onCreate_fetchAnimals() {
        new DiscoverViewModel(animalRepository);

        verify(animalRepository).fetchAnimals();
    }

    @Test
    public void fetchAnimals_onNext_outputTempText() {
        DiscoverViewModel subject = new DiscoverViewModel(animalRepository);

        assertThat(subject.tempOutput.get()).isEqualTo("got it");
    }
}