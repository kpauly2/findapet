package tech.pauly.findapet.data;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.FetchAnimalsRequest;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        when(animalService.fetchAnimals(anyString(),
                                        anyString(),
                                        anyString(),
                                        anyInt(),
                                        anyInt(),
                                        anyString(),
                                        anyString(),
                                        anyString())).thenReturn(Single.just(animalListResponse));
        when(observableHelper.applySingleSchedulers()).thenReturn(single -> single);

        subject = new AnimalRepository(animalService, observableHelper);
    }

    @Test
    public void fetchAnimals_returnsAnimalListForCorrectAnimalTypeWithSchedulers() {
        Filter filter = new Filter();
        filter.setSex(Sex.MALE);
        filter.setAge(Age.ADULT);
        filter.setSize(AnimalSize.LARGE);
        FetchAnimalsRequest request = new FetchAnimalsRequest(AnimalType.CAT, 0, "zipcode", filter);
        TestObserver<AnimalListResponse> observer = subject.fetchAnimals(request).test();

        verify(animalService).fetchAnimals(eq("zipcode"),
                                           anyString(),
                                           eq("cat"),
                                           eq(0),
                                           eq(20),
                                           eq("M"),
                                           eq("Adult"),
                                           eq("L"));
        observer.assertValues(animalListResponse)
                .assertComplete();
        verify(observableHelper).applySingleSchedulers();
    }
}