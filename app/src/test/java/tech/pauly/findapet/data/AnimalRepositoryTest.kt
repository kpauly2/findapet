package tech.pauly.findapet.data

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.SingleTransformer
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.data.models.*

class AnimalRepositoryTest {

    private val animalService: AnimalService = mock()
    private val observableHelper: ObservableHelper = mock()

    private var animalListResponse: AnimalListResponse = mock()
    private lateinit var subject: AnimalRepository

    @Before
    fun setup() {
        whenever(animalService.fetchAnimals(any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(Single.just(animalListResponse))
        whenever(observableHelper.applySingleSchedulers<Any>()).thenReturn(SingleTransformer { it })

        subject = AnimalRepository(animalService, observableHelper)
    }

    @Test
    fun fetchAnimals_returnAnimalListNotNullOrEmpty_returnsAnimalListForCorrectAnimalTypeWithSchedulers() {
        val request = setupFilterRequest()
        whenever(animalListResponse.animalList).thenReturn(listOf(mock()))

        val observer = subject.fetchAnimals(request).test()

        verify(animalService).fetchAnimals(eq("zipcode"),
                any(),
                eq("cat"),
                eq(0),
                eq(20),
                eq("M"),
                eq("Adult"),
                eq("L"),
                eq("Calico"))
        observer.assertValues(animalListResponse).assertComplete()
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun fetchAnimals_returnAnimalListNull_returnExceptionForNoAnimals() {
        val request = setupFilterRequest()
        whenever(animalListResponse.animalList).thenReturn(null)

        val observer = subject.fetchAnimals(request).test()

        observer.assertError(PetfinderException(StatusCode.ERR_NO_ANIMALS))
    }

    @Test
    fun fetchAnimals_returnAnimalListEmpty_returnExceptionForNoAnimals() {
        val request = setupFilterRequest()
        whenever(animalListResponse.animalList).thenReturn(emptyList())

        val observer = subject.fetchAnimals(request).test()

        observer.assertError(PetfinderException(StatusCode.ERR_NO_ANIMALS))
    }

    private fun setupFilterRequest(): FetchAnimalsRequest {
        val filter = Filter().apply {
            sex = Sex.MALE
            age = Age.ADULT
            size = AnimalSize.LARGE
            breed = "Calico"
        }
        return FetchAnimalsRequest(AnimalType.CAT, 0, "zipcode", filter)
    }
}