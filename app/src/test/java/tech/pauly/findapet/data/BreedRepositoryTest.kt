package tech.pauly.findapet.data

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.SingleTransformer
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import tech.pauly.findapet.data.models.AnimalType
import tech.pauly.findapet.data.models.BreedListResponse

class BreedRepositoryTest {

    private val breedService: BreedService = mock()
    private val observableHelper: ObservableHelper = mock()

    private var breedListResponse: BreedListResponse = mock()
    private lateinit var subject: BreedRepository

    @Before
    fun setup() {
        whenever(breedService.fetchBreeds(any(), any())).thenReturn(Single.just(breedListResponse))
        whenever(observableHelper.applySingleSchedulers<Any>()).thenReturn(SingleTransformer { it })
        subject = BreedRepository(breedService, observableHelper)
    }

    @Test
    fun getBreedList_callsServiceForAnimalType() {
        val observer = subject.getBreedList(AnimalType.CAT).test()

        verify(breedService).fetchBreeds(any(), eq("cat"))
        observer.assertValues(breedListResponse)
                .assertComplete()
        verify(observableHelper).applySingleSchedulers<Any>()
    }
}