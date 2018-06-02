package tech.pauly.findapet.discover;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Completable;
import io.reactivex.Single;
import tech.pauly.findapet.data.FilterRepository;
import tech.pauly.findapet.data.models.Filter;
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
    private Filter filter;

    private FilterViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(filterRepository.getCurrentFilter()).thenReturn(Single.just(filter));
        when(filterRepository.insertFilter(any(Filter.class))).thenReturn(Completable.complete());
        subject = new FilterViewModel(filterRepository, eventBus);
    }

    @Test
    public void loadCurrentFilter_sexIs0_populatesScreenForFilter() {
        when(filter.getSex()).thenReturn(0);

        subject.loadCurrentFilter();

        assertThat(subject.maleChecked.get()).isTrue();
        assertThat(subject.femaleChecked.get()).isTrue();
    }

    @Test
    public void loadCurrentFilter_sexIs1_populatesScreenForFilter() {
        when(filter.getSex()).thenReturn(1);

        subject.loadCurrentFilter();

        assertThat(subject.maleChecked.get()).isTrue();
        assertThat(subject.femaleChecked.get()).isFalse();
    }

    @Test
    public void loadCurrentFilter_sexIs2_populatesScreenForFilter() {
        when(filter.getSex()).thenReturn(2);

        subject.loadCurrentFilter();

        assertThat(subject.maleChecked.get()).isFalse();
        assertThat(subject.femaleChecked.get()).isTrue();
    }

    @Test
    public void saveFilter_bothSexesChecked_insertsCorrectFilterAndFinish() {
        subject.maleChecked.set(true);
        subject.femaleChecked.set(true);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(0);
        verify(eventBus).send(ActivityEvent.build(this).finishActivity());
    }

    @Test
    public void saveFilter_neitherSexChecked_insertsCorrectFilterAndFinish() {
        subject.maleChecked.set(false);
        subject.femaleChecked.set(false);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(0);
        verify(eventBus).send(ActivityEvent.build(this).finishActivity());
    }

    @Test
    public void saveFilter_onlyMaleChecked_insertsCorrectFilterAndFinish() {
        subject.maleChecked.set(true);
        subject.femaleChecked.set(false);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(1);
        verify(eventBus).send(ActivityEvent.build(this).finishActivity());
    }

    @Test
    public void saveFilter_onlyFemaleChecked_insertsCorrectFilterAndFinish() {
        subject.maleChecked.set(false);
        subject.femaleChecked.set(true);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(2);
        verify(eventBus).send(ActivityEvent.build(this).finishActivity());
    }
}