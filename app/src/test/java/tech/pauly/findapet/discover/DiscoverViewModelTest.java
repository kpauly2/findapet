package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tech.pauly.findapet.discover.DiscoverViewModel;
import tech.pauly.findapet.repository.AnimalRepository;

import static org.mockito.Mockito.verify;

public class DiscoverViewModelTest {

    @Mock
    AnimalRepository animalRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onCreate_fetchAnimals() {
        new DiscoverViewModel(animalRepository);

        verify(animalRepository).fetchAnimals();
    }
}