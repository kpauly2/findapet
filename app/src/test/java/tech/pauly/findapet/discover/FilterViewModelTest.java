package tech.pauly.findapet.discover;

import android.view.View;
import android.widget.ToggleButton;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import tech.pauly.findapet.data.BreedRepository;
import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.data.models.BreedListResponse;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.datastore.FilterAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilterViewModelTest {

    @Mock
    private FilterRepository filterRepository;

    @Mock
    private BreedRepository breedRepository;

    @Mock
    private ViewEventBus eventBus;

    @Mock
    private TransientDataStore dataStore;

    @Mock
    private FilterAdapter filterAdapter;

    @Mock
    private Filter filter;

    @Mock
    private BreedListResponse breedListResponse;

    private FilterViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(filterRepository.getCurrentFilter()).thenReturn(Single.just(filter));
        when(filterRepository.insertFilter(any(Filter.class))).thenReturn(Completable.complete());
        when(breedRepository.getBreedList(any(AnimalType.class))).thenReturn(Single.just(breedListResponse));
        subject = new FilterViewModel(filterRepository, breedRepository, eventBus, dataStore, filterAdapter);
    }

    @Test
    public void create_setViewModelOnAdapter() {
        verify(filterAdapter).setViewModel(subject);
    }

    @Test
    public void getAdapter_returnsAdapter() {
        assertThat(subject.getAdapter()).isEqualTo(filterAdapter);
    }

    @Test
    public void loadCurrentFilter_populatesScreenForFilter() {
        subject.selectedSex.set(Sex.FEMALE);
        subject.selectedAge.set(Age.YOUNG);
        subject.selectedSize.set(AnimalSize.SMALL);
        when(filter.getSex()).thenReturn(Sex.MALE);
        when(filter.getAge()).thenReturn(Age.ADULT);
        when(filter.getSize()).thenReturn(AnimalSize.LARGE);
        when(filter.getBreed()).thenReturn("Calico");

        subject.loadCurrentFilter();

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.MALE);
        assertThat(subject.selectedAge.get()).isEqualTo(Age.ADULT);
        assertThat(subject.selectedSize.get()).isEqualTo(AnimalSize.LARGE);
        assertThat(subject.selectedBreed.get()).isEqualTo("Calico");
    }

    @Test
    public void checkSex_buttonNotChecked_setSexToU() {
        subject.selectedSex.set(Sex.FEMALE);
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(false);

        subject.checkSex(button, Sex.MALE);

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.MISSING);
    }

    @Test
    public void checkSex_buttonChecked_setSexToButtonSex() {
        subject.selectedSex.set(Sex.FEMALE);
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(true);

        subject.checkSex(button, Sex.MALE);

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.MALE);
    }

    @Test
    public void checkAge_buttonNotChecked_setAgeToMissing() {
        subject.selectedAge.set(Age.YOUNG);
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(false);

        subject.checkAge(button, Age.ADULT);

        assertThat(subject.selectedAge.get()).isEqualTo(Age.MISSING);
    }

    @Test
    public void checkAge_buttonChecked_setAgeToButtonAge() {
        subject.selectedAge.set(Age.YOUNG);
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(true);

        subject.checkAge(button, Age.ADULT);

        assertThat(subject.selectedAge.get()).isEqualTo(Age.ADULT);
    }

    @Test
    public void checkSize_buttonNotChecked_setSizeToMissing() {
        subject.selectedSize.set(AnimalSize.MEDIUM);
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(false);

        subject.checkSize(button, AnimalSize.SMALL);

        assertThat(subject.selectedSize.get()).isEqualTo(AnimalSize.MISSING);
    }

    @Test
    public void checkSize_buttonChecked_setSizeToButtonSize() {
        subject.selectedSize.set(AnimalSize.MEDIUM);
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(true);

        subject.checkSize(button, AnimalSize.SMALL);

        assertThat(subject.selectedSize.get()).isEqualTo(AnimalSize.SMALL);
    }

    @Test
    public void checkBreed_buttonNotChecked_setBreedToEmpty() {
        subject.selectedBreed.set("Calico");
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(false);

        subject.checkBreed(button, "Ragdoll");

        assertThat(subject.selectedBreed.get()).isEqualTo("");
    }

    @Test
    public void checkBreed_buttonChecked_setBreedToButtonBreed() {
        subject.selectedBreed.set("Calico");
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(true);

        subject.checkBreed(button, "Ragdoll");

        assertThat(subject.selectedBreed.get()).isEqualTo("Ragdoll");
    }

    @Test
    public void saveFilter_finishesScreen() {
        subject.saveFilter(mock(View.class));

        verify(eventBus).send(new ActivityEvent(subject, null, true));
    }

    @Test
    public void saveFilter_insertsNewFilterForCurrentSelectedItems() {
        subject.selectedSex.set(Sex.MALE);
        subject.selectedAge.set(Age.ADULT);
        subject.selectedSize.set(AnimalSize.LARGE);
        subject.selectedBreed.set("Calico");
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(Sex.MALE);
        assertThat(captor.getValue().getAge()).isEqualTo(Age.ADULT);
        assertThat(captor.getValue().getSize()).isEqualTo(AnimalSize.LARGE);
        assertThat(captor.getValue().getBreed()).isEqualTo("Calico");
    }

    @Test
    public void populateBreedList_noAnimalType_doNothing() {
        when(dataStore.get(FilterAnimalTypeUseCase.class)).thenReturn(null);

        subject.updateBreedList();

        verify(breedRepository, never()).getBreedList(any(AnimalType.class));
    }

    @Test
    public void populateBreedList_getsBreedListForAnimalType() {
        when(breedListResponse.getBreedList()).thenReturn(null);
        setupDataStoreWithUseCase();

        subject.updateBreedList();

        verify(breedRepository).getBreedList(AnimalType.CAT);
    }

    @Test
    public void populateBreedList_returnedBreedListNull_doNothing() {
        when(breedListResponse.getBreedList()).thenReturn(null);
        setupDataStoreWithUseCase();

        subject.updateBreedList();

        verify(filterAdapter, never()).setBreedItems(any(List.class));
    }

    @Test
    public void populateBreedList_returnedBreedListValid_showBreedList() {
        when(breedListResponse.getBreedList()).thenReturn(Arrays.asList("breed 1", "breed 2"));
        setupDataStoreWithUseCase();

        subject.updateBreedList();

        verify(filterAdapter).setBreedItems(breedListResponse.getBreedList());
    }

    @Test
    public void populateBreedList_returnedBreedListValidAndHaveSelectedBreed_showBreedListWithSelectedBreedFirst() {
        subject.selectedBreed.set("breed 2");
        when(breedListResponse.getBreedList()).thenReturn(Arrays.asList("breed 1", "breed 2"));
        setupDataStoreWithUseCase();

        subject.updateBreedList();

        verify(filterAdapter).setBreedItems(Arrays.asList("breed 2", "breed 1"));
    }


    @Test
    public void clickBreedSearch_fireScrollSubject() {
        TestObserver<Boolean> observer = subject.getScrollToViewSubject().test();

        subject.clickBreedSearch();

        observer.assertValue(true);
    }

    @Test
    public void onBreedTextChanged_fireScrollSubject() {
        TestObserver<Boolean> observer = subject.getScrollToViewSubject().test();

        subject.onBreedTextChanged("", 0, 0, 0);

        observer.assertValue(true);
    }

    @Test
    public void onBreedTextChanged_hasSelectedBreed_updateBreedItemsWithSelectedBreedFirst() {
        subject.selectedBreed.set("breed 2");
        when(breedListResponse.getBreedList()).thenReturn(Arrays.asList("breed 1", "breed 2"));
        setupDataStoreWithUseCase();
        subject.updateBreedList();
        clearInvocations(filterAdapter);

        subject.onBreedTextChanged("breed", 0, 0, 0);

        verify(filterAdapter).setBreedItems(Arrays.asList("breed 2", "breed 1"));
    }

    @Test
    public void onBreedTextChanged_updateBreedItems() {
        when(breedListResponse.getBreedList()).thenReturn(Arrays.asList("abc", "ab", "c"));
        setupDataStoreWithUseCase();
        subject.updateBreedList();

        subject.onBreedTextChanged("a", 0, 0, 0);

        verify(filterAdapter).setBreedItems(Arrays.asList("abc", "ab"));
    }

    @Test
    public void onBreedTextChanged_inputTextDifferentCapitals_updateBreedItems() {
        when(breedListResponse.getBreedList()).thenReturn(Arrays.asList("abc", "ab", "c"));
        setupDataStoreWithUseCase();
        subject.updateBreedList();

        subject.onBreedTextChanged("A", 0, 0, 0);

        verify(filterAdapter).setBreedItems(Arrays.asList("abc", "ab"));
    }

    @Test
    public void onBreedTextChanged_noMatches_updateBreedItems() {
        when(breedListResponse.getBreedList()).thenReturn(Arrays.asList("abc", "ab", "c"));
        setupDataStoreWithUseCase();
        subject.updateBreedList();

        subject.onBreedTextChanged("d", 0, 0, 0);

        verify(filterAdapter).setBreedItems(Collections.emptyList());
    }

    private void setupDataStoreWithUseCase() {
        FilterAnimalTypeUseCase useCase = mock(FilterAnimalTypeUseCase.class);
        when(useCase.getAnimalType()).thenReturn(AnimalType.CAT);
        when(dataStore.get(FilterAnimalTypeUseCase.class)).thenReturn(useCase);
    }
}