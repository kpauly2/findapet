package tech.pauly.findapet.data;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.BreedListResponse;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BreedRepositoryTest {

    @Mock
    private BreedService breedService;

    @Mock
    private ObservableHelper observableHelper;

    private BreedRepository subject;
    private BreedListResponse breedListResponse;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        breedListResponse = mock(BreedListResponse.class);
        when(breedService.fetchBreeds(anyString(), anyString())).thenReturn(Single.just(breedListResponse));
        when(observableHelper.applySingleSchedulers()).thenReturn(single -> single);
        subject = new BreedRepository(breedService, observableHelper);
    }

    @Test
    public void getBreedList_callsServiceForAnimalType() {
        TestObserver<BreedListResponse> observer = subject.getBreedList(AnimalType.CAT).test();

        verify(breedService).fetchBreeds(anyString(), eq("cat"));
        observer.assertValues(breedListResponse)
                .assertComplete();
        verify(observableHelper).applySingleSchedulers();
    }
}