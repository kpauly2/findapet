package tech.pauly.findapet.data;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Single;
import tech.pauly.findapet.data.models.Filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilterRepositoryTest {

    @Mock
    private FilterDatabase filterDatabase;

    @Mock
    private ObservableHelper observableHelper;

    @Mock
    private FilterDao filterDao;

    @Mock
    private Filter filter;

    private FilterRepository subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(observableHelper.applySchedulers()).thenReturn(observable -> observable);
        when(filterDatabase.filterDao()).thenReturn(filterDao);
        when(filterDao.findById(anyLong())).thenReturn(Single.just(filter));
        when(filter.getId()).thenReturn(1L);
        when(filterDao.insert(filter)).thenReturn(1L);

        subject = new FilterRepository(filterDatabase, observableHelper);
    }

    @Test
    public void getCurrentFilter_currentIdNotPresent_returnError() {
        subject.getCurrentFilter().test().assertError(IllegalStateException.class);
    }

    @Test
    public void getCurrentFilter_currentIdPresent_callDatabaseAndReturnFilterForCurrentId() {
        subject.currentFilterId = 1L;

        subject.getCurrentFilter().test().assertValues(filter);
        verify(filterDatabase.filterDao()).findById(1L);
    }

    @Test
    public void insertFilter_insertFilterAndSetCurrentFilterId() {
        subject.insertFilter(filter).test();

        verify(filterDatabase.filterDao()).insert(filter);
        assertThat(subject.currentFilterId).isEqualTo(1L);
    }

    @Test
    public void getCurrentFilterAndNoFilterIfEmpty_currentIdNotPresent_returnEmptyFilter() {
        subject.getCurrentFilterAndNoFilterIfEmpty().test().assertValues(new Filter());
    }

    @Test
    public void getCurrentFilterAndNoFilterIfEmpty_currentIdPresent_returnFilter() {
        subject.currentFilterId = 1L;

        subject.getCurrentFilterAndNoFilterIfEmpty().test().assertValues(filter);
        verify(filterDatabase.filterDao()).findById(1L);
    }
}