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
        subject.selectedSex.set(Sex.F);
        subject.selectedAge.set(Age.YOUNG);
        when(filter.getSex()).thenReturn(Sex.M);
        when(filter.getAge()).thenReturn(Age.ADULT);

        subject.loadCurrentFilter();

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.M);
        assertThat(subject.selectedAge.get()).isEqualTo(Age.ADULT);
    }

    @Test
    public void checkSex_buttonNotChecked_setSexToU() {
        subject.selectedSex.set(Sex.F);
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(false);

        subject.checkSex(button, Sex.M);

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.U);
    }

    @Test
    public void checkSex_buttonChecked_setSexToButtonSex() {
        subject.selectedSex.set(Sex.F);
        ToggleButton button = mock(ToggleButton.class);
        when(button.isChecked()).thenReturn(true);

        subject.checkSex(button, Sex.M);

        assertThat(subject.selectedSex.get()).isEqualTo(Sex.M);
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
    public void saveFilter_savesUseCaseAndFinishesScreen() {
        subject.saveFilter(mock(View.class));

        verify(eventBus).send(ActivityEvent.build(this).finishActivity());
    }

    @Test
    public void saveFilter_insertsNewFilterForCurrentSelectedItems() {
        subject.selectedSex.set(Sex.M);
        subject.selectedAge.set(Age.ADULT);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(Sex.M);
        assertThat(captor.getValue().getAge()).isEqualTo(Age.ADULT);
    }
}