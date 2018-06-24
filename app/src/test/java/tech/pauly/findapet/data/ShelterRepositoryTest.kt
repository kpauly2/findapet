package tech.pauly.findapet.data

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.SingleTransformer
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.data.models.ShelterListResponse


class ShelterRepositoryTest {

    private val shelterService: ShelterService = mock()
    private val observableHelper: ObservableHelper = mock()

    private var shelterListResponse: ShelterListResponse = mock()
    private lateinit var subject: ShelterRepository

    @Before
    fun setup() {
        whenever(shelterService.fetchShelters(any(), any(), any())).thenReturn(Single.just(shelterListResponse))
        whenever(observableHelper.applySingleSchedulers<Any>()).thenReturn(SingleTransformer { it })

        subject = ShelterRepository(shelterService, observableHelper)
    }

    @Test
    fun fetchShelters_returnsShelterListWithSchedulers() {
        val observer = subject.fetchShelters("zipcode").test()

        verify(shelterService).fetchShelters(eq("zipcode"), any(), eq(20))
        observer.assertValues(shelterListResponse).assertComplete()
        verify(observableHelper).applySingleSchedulers<Any>()
    }
}