package tech.pauly.findapet.data

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.SingleTransformer
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.data.models.*

class AnimalRepositoryTest {

    @MockK
    private lateinit var animalService: AnimalService

    @MockK
    private lateinit var observableHelper: ObservableHelper

    @MockK
    private lateinit var animalListResponse: AnimalListResponse

    @MockK
    private lateinit var singleAnimalResponse: SingleAnimalResponse

    @MockK
    private lateinit var header: Header

    private lateinit var subject: AnimalRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { animalService.fetchAnimals(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Single.just(animalListResponse)
        every { observableHelper.applySingleSchedulers<Any>() } returns SingleTransformer { it }
        every { animalListResponse.header } returns header
        every { singleAnimalResponse.header } returns header

        subject = AnimalRepository(animalService, observableHelper)
    }

    @Test
    fun fetchAnimals_returnAnimalListNotNullOrEmpty_returnsAnimalListForCorrectAnimalTypeWithSchedulers() {
        val request = setupFilterRequest()
        val status = mockk<Status> {
            every { code } returns StatusCode.PFAPI_OK
        }
        every { header.status } returns status
        every { animalListResponse.animalList } returns listOf(mockk())

        val observer = subject.fetchAnimals(request).test()

        observer.assertValues(animalListResponse).assertComplete()
        verify {
            animalService.fetchAnimals("zipcode",
                    any(),
                    "cat",
                    0,
                    20,
                    "M",
                    "Adult",
                    "L",
                    "Calico")
            observableHelper.applySingleSchedulers<Any>()
        }
    }

    @Test
    fun fetchAnimals_statusCodeOK_returnsOnNext() {
        val request = setupFilterRequest()
        every { animalListResponse.animalList } returns listOf(mockk())
        val status = mockk<Status> {
            every { code } returns StatusCode.PFAPI_OK
        }
        every { header.status } returns status

        val observer = subject.fetchAnimals(request).test()

        observer.assertValues(animalListResponse).assertComplete()
    }

    @Test
    fun fetchAnimals_statusCodeNotOK_returnsPetfinderExceptionForStatusCode() {
        val request = setupFilterRequest()
        every { animalListResponse.animalList } returns listOf(mockk())
        val status = mockk<Status> {
            every { code } returns StatusCode.PFAPI_ERR_INTERNAL
        }
        every { header.status } returns status

        val observer = subject.fetchAnimals(request).test()

        observer.assertError(PetfinderException(StatusCode.PFAPI_ERR_INTERNAL))
    }

    @Test
    fun fetchAnimals_returnAnimalListNull_returnExceptionForNoAnimals() {
        val request = setupFilterRequest()
        val status = mockk<Status> {
            every { code } returns StatusCode.PFAPI_OK
        }
        every { header.status } returns status
        every { animalListResponse.animalList } returns null

        val observer = subject.fetchAnimals(request).test()

        observer.assertError(PetfinderException(StatusCode.ERR_NO_ANIMALS))
    }

    @Test
    fun fetchAnimals_returnAnimalListEmpty_returnExceptionForNoAnimals() {
        val request = setupFilterRequest()
        val status   = mockk<Status> {
            every { code } returns StatusCode.PFAPI_OK
        }
        every { header.status } returns status
        every { animalListResponse.animalList } returns emptyList()

        val observer = subject.fetchAnimals(request).test()

        observer.assertError(PetfinderException(StatusCode.ERR_NO_ANIMALS))
    }

    @Test
    fun fetchAnimal_animalIdFound_returnFetchedAnimalAndHeader() {
        val localAnimal = mockk<LocalAnimal> { every { id } returns 10 }
        val internetAnimal = mockk<InternetAnimal> { every { id } returns 10 }
        every { singleAnimalResponse.animal } returns internetAnimal
        every { animalService.fetchAnimal(any(), "10") } returns Single.just(singleAnimalResponse)

        val observer = subject.fetchAnimal(localAnimal).test()

        observer.assertValues(AnimalResponseWrapper(internetAnimal, header))
                .assertComplete()
        verify { observableHelper.applySingleSchedulers<Any>() }
    }

    @Test
    fun fetchAnimal_animalInResponseIsNull_returnOriginalAnimalAndHeader() {
        val localAnimal = mockk<LocalAnimal> { every { id } returns 10 }
        every { singleAnimalResponse.animal } returns null
        every { animalService.fetchAnimal(any(), "10") } returns Single.just(singleAnimalResponse)

        val observer = subject.fetchAnimal(localAnimal).test()

        observer.assertValues(AnimalResponseWrapper(localAnimal, header))
                .assertComplete()
        verify { observableHelper.applySingleSchedulers<Any>() }
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