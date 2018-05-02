package tech.pauly.findapet.data;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import tech.pauly.findapet.data.models.AnimalListResponse;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnimalRepositoryTest {

    @Mock
    private AnimalService animalService;

    @Mock
    private ObservableHelper observableHelper;

    private AnimalRepository subject;
    private AnimalListResponse animalListResponse;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        animalListResponse = mock(AnimalListResponse.class);
        when(animalService.fetchAnimals(anyString(), anyString(), anyString())).thenReturn(Observable.just(animalListResponse));
        when(observableHelper.applySchedulers()).thenReturn(observable -> observable);

        subject = new AnimalRepository(animalService, observableHelper);
    }

    @Test
    public void fetchAnimals_returnsAnimalListWithSchedulers() {
        TestObserver<AnimalListResponse> observer = subject.fetchAnimals().test();

        observer.assertValues(animalListResponse)
                .assertComplete();
        verify(observableHelper).applySchedulers();
    }
}