package tech.pauly.findapet.discover;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import io.reactivex.Single;
import tech.pauly.findapet.data.BreedRepository;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.BreedListResponse;
import tech.pauly.findapet.shared.datastore.BreedAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BreedViewModelTest {

    @Mock
    private BreedRepository breedRepository;

    @Mock
    private TransientDataStore dataStore;

    @Mock
    private BreedListResponse breedListResponse;

    private BreedViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(breedRepository.getBreedList(any(AnimalType.class))).thenReturn(Single.just(breedListResponse));
        subject = new BreedViewModel(breedRepository, dataStore);
    }

    @Test
    public void updateBreedList_noAnimalType_doNothing() {
        when(dataStore.get(BreedAnimalTypeUseCase.class)).thenReturn(null);

        subject.updateBreedList();

        verify(breedRepository, never()).getBreedList(any(AnimalType.class));
    }

    @Test
    public void updateBreedList_getsBreedListForAnimalType() {
        when(breedListResponse.getBreedList()).thenReturn(null);
        setupDataStoreWithUseCase();

        subject.updateBreedList();

        verify(breedRepository).getBreedList(AnimalType.CAT);
    }

    @Test
    public void updateBreedList_returnedBreedListNull_doNothing() {
        when(breedListResponse.getBreedList()).thenReturn(null);
        setupDataStoreWithUseCase();

        subject.updateBreedList();

        assertThat(subject.breedList).hasSize(0);
    }

    @Test
    public void updateBreedList_returnedBreedListValid_showBreedList() {
        when(breedListResponse.getBreedList()).thenReturn(Arrays.asList("breed 1", "breed 2"));
        setupDataStoreWithUseCase();

        subject.updateBreedList();

        assertThat(subject.breedList).isEqualTo(breedListResponse.getBreedList());
    }

    private void setupDataStoreWithUseCase() {
        BreedAnimalTypeUseCase useCase = mock(BreedAnimalTypeUseCase.class);
        when(useCase.getAnimalType()).thenReturn(AnimalType.CAT);
        when(dataStore.get(BreedAnimalTypeUseCase.class)).thenReturn(useCase);
    }
}