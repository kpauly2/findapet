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
    public void loadCurrentFilter_sexIsU_populatesScreenForFilter() {
        when(filter.getSex()).thenReturn(Sex.U);

        subject.loadCurrentFilter();

        assertThat(subject.maleChecked.get()).isTrue();
        assertThat(subject.femaleChecked.get()).isTrue();
    }

    @Test
    public void loadCurrentFilter_sexIsM_populatesScreenForFilter() {
        when(filter.getSex()).thenReturn(Sex.M);

        subject.loadCurrentFilter();

        assertThat(subject.maleChecked.get()).isTrue();
        assertThat(subject.femaleChecked.get()).isFalse();
    }

    @Test
    public void loadCurrentFilter_sexIsF_populatesScreenForFilter() {
        when(filter.getSex()).thenReturn(Sex.F);

        subject.loadCurrentFilter();

        assertThat(subject.maleChecked.get()).isFalse();
        assertThat(subject.femaleChecked.get()).isTrue();
    }

    @Test
    public void saveFilter_savesUseCaseAndFinishesScreen() {
        subject.saveFilter(mock(View.class));

        verify(eventBus).send(ActivityEvent.build(this).finishActivity());
    }

    @Test
    public void saveFilter_bothSexesChecked_insertsCorrectFilter() {
        subject.maleChecked.set(true);
        subject.femaleChecked.set(true);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(Sex.U);
    }

    @Test
    public void saveFilter_neitherSexChecked_insertsCorrectFilter() {
        subject.maleChecked.set(false);
        subject.femaleChecked.set(false);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(Sex.U);
    }

    @Test
    public void saveFilter_onlyMaleChecked_insertsCorrectFilter() {
        subject.maleChecked.set(true);
        subject.femaleChecked.set(false);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(Sex.M);
    }

    @Test
    public void saveFilter_onlyFemaleChecked_insertsCorrectFilter() {
        subject.maleChecked.set(false);
        subject.femaleChecked.set(true);
        ArgumentCaptor<Filter> captor = ArgumentCaptor.forClass(Filter.class);

        subject.saveFilter(mock(View.class));

        verify(filterRepository).insertFilter(captor.capture());
        assertThat(captor.getValue().getSex()).isEqualTo(Sex.F);
    }
}