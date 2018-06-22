package tech.pauly.findapet.data

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.SingleTransformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import tech.pauly.findapet.data.models.Filter

class FilterRepositoryTest {

    private val filterDatabase: FilterDatabase = mock()
    private val observableHelper: ObservableHelper = mock()
    private val filterDao: FilterDao = mock()
    private val filter: Filter = mock()

    private lateinit var subject: FilterRepository

    @Before
    fun setup() {
        whenever(observableHelper.applySingleSchedulers<Any>()).thenReturn(SingleTransformer { it })
        whenever(filterDatabase.filterDao()).thenReturn(filterDao)
        whenever(filterDao.findById(anyLong())).thenReturn(Single.just(filter))
        whenever(filter.id).thenReturn(1L)
        whenever(filterDao.insert(filter)).thenReturn(1L)

        subject = FilterRepository(filterDatabase, observableHelper)
    }

    @Test
    fun getCurrentFilter_currentIdNotPresent_returnError() {
        subject.currentFilter.test().assertError(IllegalStateException::class.java)
    }

    @Test
    fun getCurrentFilter_currentIdPresent_callDatabaseAndReturnFilterForCurrentId() {
        subject._currentFilter = 1L

        subject.currentFilter.test().assertValues(filter)
        verify(filterDatabase.filterDao()).findById(1L)
    }

    @Test
    fun insertFilter_insertFilterAndSetCurrentFilterId() {
        subject.insertFilter(filter).test()

        verify(filterDatabase.filterDao()).insert(filter)
        assertThat(subject._currentFilter).isEqualTo(1L)
    }

    @Test
    fun getCurrentFilterAndNoFilterIfEmpty_currentIdNotPresent_returnEmptyFilter() {
        subject.currentFilterAndNoFilterIfEmpty.test().assertValues(Filter())
    }

    @Test
    fun getCurrentFilterAndNoFilterIfEmpty_currentIdPresent_returnFilter() {
        subject._currentFilter = 1L

        subject.currentFilterAndNoFilterIfEmpty.test().assertValues(filter)
        verify(filterDatabase.filterDao()).findById(1L)
    }
}