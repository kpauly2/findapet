package tech.pauly.findapet.data;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.Animal;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.FetchAnimalsRequest;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.data.models.StatusCode;

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
                                        anyString(),
                                        anyString())).thenReturn(Single.just(animalListResponse));
        when(observableHelper.applySingleSchedulers()).thenReturn(single -> single);

        subject = new AnimalRepository(animalService, observableHelper);
    }

    @Test
    public void fetchAnimals_returnAnimalListNotNullOrEmpty_returnsAnimalListForCorrectAnimalTypeWithSchedulers() {
        FetchAnimalsRequest request = setupFilterRequest();
        when(animalListResponse.getAnimalList()).thenReturn(Collections.singletonList(mock(Animal.class)));

        TestObserver<AnimalListResponse> observer = subject.fetchAnimals(request).test();

        verify(animalService).fetchAnimals(eq("zipcode"),
                                           anyString(),
                                           eq("cat"),
                                           eq(0),
                                           eq(20),
                                           eq("M"),
                                           eq("Adult"),
                                           eq("L"),
                                           eq("Calico"));
        observer.assertValues(animalListResponse)
                .assertComplete();
        verify(observableHelper).applySingleSchedulers();
    }

    @Test
    public void fetchAnimals_returnAnimalListNull_returnExceptionForNoAnimals() {
        FetchAnimalsRequest request = setupFilterRequest();
        when(animalListResponse.getAnimalList()).thenReturn(null);

        TestObserver<AnimalListResponse> observer = subject.fetchAnimals(request).test();

        observer.assertError(new PetfinderException(StatusCode.ERR_NO_ANIMALS));
    }

    @Test
    public void fetchAnimals_returnAnimalListEmpty_returnExceptionForNoAnimals() {
        FetchAnimalsRequest request = setupFilterRequest();
        when(animalListResponse.getAnimalList()).thenReturn(Collections.emptyList());

        TestObserver<AnimalListResponse> observer = subject.fetchAnimals(request).test();

        observer.assertError(new PetfinderException(StatusCode.ERR_NO_ANIMALS));
    }

    @NonNull
    private FetchAnimalsRequest setupFilterRequest() {
        Filter filter = new Filter();
        filter.setSex(Sex.MALE);
        filter.setAge(Age.ADULT);
        filter.setSize(AnimalSize.LARGE);
        filter.setBreed("Calico");
        return new FetchAnimalsRequest(AnimalType.CAT, 0, "zipcode", filter);
    }
}