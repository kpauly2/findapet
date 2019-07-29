package tech.pauly.old.data

import androidx.room.EmptyResultSetException
import com.nhaarman.mockito_kotlin.*
import io.reactivex.CompletableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer
import org.junit.Before
import org.junit.Test
import tech.pauly.old.data.models.*


class ShelterRepositoryTest {

    private val shelterService: ShelterService = mock()
    private val observableHelper: ObservableHelper = mock()
    private val database: FavoriteDatabase = mock()

    private val contact: Shelter = mock()
    private val animal: Animal = mock()
    private val shelter: Shelter = mock()
    private val shelterDao: ShelterDao = mock()
    private var shelterListResponse: ShelterListResponse = mock()
    private lateinit var subject: ShelterRepository

    @Before
    fun setup() {
        whenever(shelterService.fetchShelters(any(), any(), any())).thenReturn(Single.just(shelterListResponse))
        whenever(observableHelper.applySingleSchedulers<Any>()).thenReturn(SingleTransformer { it })
        whenever(observableHelper.applyCompletableSchedulers()).thenReturn(CompletableTransformer { it })
        whenever(database.shelterDao()).thenReturn(shelterDao)
        whenever(animal.shelterId).thenReturn("shelterId")
        whenever(animal.contact).thenReturn(contact)
        whenever(shelter.id).thenReturn("shelterId")

        subject = ShelterRepository(shelterService, observableHelper, database)
    }

    @Test
    fun fetchShelters_returnsSheltersFromNetwork() {
        val observer = subject.fetchShelters("zipcode").test()

        verify(shelterService).fetchShelters(eq("zipcode"), any(), eq(20))
        observer.assertValues(shelterListResponse).assertComplete()
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun fetchShelter_shelterInDatabase_returnsShelter() {
        whenever(shelterDao.getShelter("shelterId")).thenReturn(Single.just(shelter))

        val observer = subject.fetchShelter(animal).test()

        observer.assertValues(shelter).assertComplete()
        verify(shelterService, never()).fetchShelter(any(), any())
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun fetchShelter_shelterNotInDatabaseAndIsOnline_returnsShelter() {
        whenever(shelterDao.getShelter("shelterId")).thenReturn(Single.error(EmptyResultSetException("")))
        val response: SingleShelterResponse = mock { response ->
            on { response.shelter }.thenReturn(shelter)
        }
        whenever(shelterService.fetchShelter(any(), eq("shelterId"))).thenReturn(Single.just(response))

        val observer = subject.fetchShelter(animal).test()

        observer.assertValues(shelter).assertComplete()
        verify(shelterService).fetchShelter(any(), eq("shelterId"))
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun fetchShelter_shelterNotInDatabaseAndGetsErrorFromBackend_returnsAnimalsStoredContact() {
        whenever(shelterDao.getShelter("shelterId")).thenReturn(Single.error(EmptyResultSetException("")))
        val status: Status = mock {
            on { code }.thenReturn(StatusCode.PFAPI_ERR_UNAUTHORIZED)
        }
        val header: Header = mock { header ->
            on { header.status }.thenReturn(status)
        }
        val response: SingleShelterResponse = mock { response ->
            on { response.header }.thenReturn(header)
        }
        whenever(shelterService.fetchShelter(any(), eq("shelterId"))).thenReturn(Single.just(response))

        val observer = subject.fetchShelter(animal).test()

        observer.assertValues(contact).assertComplete()
        verify(observableHelper).applySingleSchedulers<Any>()
    }

    @Test
    fun insertShelterRecordForAnimal_fetchesShelterAndInsertsItToDatabase() {

    }

    @Test
    fun deleteShelterIfNecessary_shelterInSavedAnimalSheltersMultipleTimes_doNotDelete() {

    }

    @Test
    fun deleteShelterIfNecessary_shelterInSavedAnimalSheltersOnce_deleteShelter() {

    }
}