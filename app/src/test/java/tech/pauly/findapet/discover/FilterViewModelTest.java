package tech.pauly.findapet.discover;

import android.view.View;
import android.widget.ToggleButton;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Completable;
import io.reactivex.Single;
import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.models.Age;
import tech.pauly.findapet.data.models.AnimalSize;
import tech.pauly.findapet.data.models.Filter;
import tech.pauly.findapet.data.models.Sex;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilterViewModelTest {

    @Mock
    private FilterRepository filterRepository;

    @Mock
    private ViewEventBus eventBus;

    @Mock
    private TransientDataStore dataStore;

    @Mock
    private Filter filter;

    private FilterViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(filterRepository.getCurrentFilter()).thenReturn(Single.just(filter));
        when(filterRepository.insertFilter(any(Filter.class))).thenReturn(Completable.complete());
        subject = new FilterViewModel(filterRepository, eventBus, dataStore);
    }

    @Test
    public void loadCurrentFilter_populatesScreenForFilter() {
        subject.selectedSex.set(Sex.FEMALE);
        subject.selectedAge.set(Age.YOUNG);
        subject.selectedSize.set(AnimalSize.SMALL);
        when(filter.getSex()).thenReturn(Sex.MALE);
        when(filter.getAge()).thenReturn(Age.ADULT);
        when(filter.getSize()).thenReturn(AnimalSize.LARGE);

        subject.loadCurrentFilter();

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.MALE);
        assertThat(subject.selectedAge.get()).isEqualTo(Age.ADULT);
        assertThat(subject.selectedSize.get()).isEqualTo(AnimalSize.LARGE);
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
    public void saveFilter_savesUseCaseAndFinishesScreen() {
        subject.saveFilter(mock(View.class));

        verify(eventBus).send(ActivityEvent.build(this).finishActivity());
    }

    @Test
    public void saveFilter_insertsNewFilterForCurrentSelectedItems() {
        subject.selectedSex.set(Sex.MALE);
        subject.selectedAge.set(Age.ADULT);
        subject.selectedSize.set(AnimalSize.LARGE);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(Sex.MALE);
        assertThat(captor.getValue().getAge()).isEqualTo(Age.ADULT);
        assertThat(captor.getValue().getSize()).isEqualTo(AnimalSize.LARGE);
    }

    @Test
    public void onClickBreedSearch_launchesBreedActivity() {
        subject.clickBreedSearch(null);

        verify(eventBus).send(ActivityEvent.build(subject).startActivity(BreedActivity.class));
    }
}