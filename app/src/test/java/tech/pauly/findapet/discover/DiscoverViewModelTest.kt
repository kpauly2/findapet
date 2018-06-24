package tech.pauly.findapet.discover

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Address
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import tech.pauly.findapet.R
import tech.pauly.findapet.data.AnimalRepository
import tech.pauly.findapet.data.FilterRepository
import tech.pauly.findapet.data.PetfinderException
import tech.pauly.findapet.data.models.*
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.PermissionHelper
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.DiscoverAnimalTypeUseCase
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.PermissionEvent
import tech.pauly.findapet.shared.events.ViewEventBus
import java.util.ArrayList

class DiscoverViewModelTest {

    private val listAdapter: AnimalListAdapter = mock()
    private val animalListItemFactory: AnimalListItemViewModel.Factory = mock()
    private val animalRepository: AnimalRepository = mock()
    private val dataStore: TransientDataStore = mock()
    private val permissionHelper: PermissionHelper = mock()
    private val eventBus: ViewEventBus = mock()
    private val locationHelper: LocationHelper = mock()
    private val animalListResponse: AnimalListResponse = mock()
    private val resourceProvider: ResourceProvider = mock {
        on { getString(R.string.male) }.thenReturn("Male")
        on { getString(R.string.adult) }.thenReturn("Adult")
        on { getString(R.string.large) }.thenReturn("Large")
        on { getString(R.string.chip_near_location, "zipcode") }.thenReturn("Near zipcode")
        on { getString(R.string.chip_near_location, "zipcode2") }.thenReturn("Near zipcode2")
    }
    private val filterRepository: FilterRepository = mock()

    private val fetchAnimalsRequest: FetchAnimalsRequest = mock()
    private val filter: Filter = mock {
        on { sex }.thenReturn(Sex.MISSING)
        on { age }.thenReturn(Age.MISSING)
        on { size }.thenReturn(AnimalSize.MISSING)
        on { breed }.thenReturn("")
    }
    private lateinit var subject: DiscoverViewModel

    @Before
    fun setup() {
        val useCase: DiscoverAnimalTypeUseCase = mock {
            on { animalType }.thenReturn(AnimalType.CAT)
        }
        whenever(dataStore[DiscoverAnimalTypeUseCase::class]).thenReturn(useCase)
        whenever(permissionHelper.hasPermissions(ACCESS_FINE_LOCATION)).thenReturn(true)
        whenever(animalRepository.fetchAnimals(any())).thenReturn(Observable.just(animalListResponse))
        val address: Address = mock {
            on { postalCode }.thenReturn("zipcode")
        }
        whenever(locationHelper.fetchCurrentLocation()).thenReturn(Observable.just(address))
        fetchAnimalsRequest.also {
            whenever(it.animalType).thenReturn(AnimalType.CAT)
            whenever(it. location).thenReturn("zipcode")
            whenever(it.lastOffset).thenReturn(0)
            whenever(it.filter).thenReturn(filter)
        }
        whenever(filterRepository.currentFilterAndNoFilterIfEmpty).thenReturn(Single.just(filter))
        whenever(dataStore[FilterUpdatedUseCase::class]).thenReturn(null)

        subject = DiscoverViewModel(listAdapter, animalListItemFactory, animalRepository, dataStore, permissionHelper, eventBus, locationHelper, resourceProvider, filterRepository)
    }

    @Test
    fun updateToolbar_updatesToolbarTitleAndOptionsMenu() {
        subject.updateToolbar()

        verify(dataStore) += DiscoverToolbarTitleUseCase(AnimalType.CAT.toolbarName)
        verify(eventBus) += OptionsMenuEvent(subject, OptionsMenuState.DISCOVER)
    }

    @Test
    fun onResume_firstLoad_loadList() {
        subject.onResume()

        verify(animalRepository).fetchAnimals(any())
    }

    @Test
    fun onResume_notFirstLoadButFilterUpdated_loadList() {
        subject.onResume()
        clearInvocations<AnimalRepository>(animalRepository)
        whenever(dataStore[FilterUpdatedUseCase::class]).thenReturn(mock())

        subject.onResume()

        verify(animalRepository).fetchAnimals(any())
    }

    @Test
    fun onResume_notFirstLoadAndFilterNotUpdated_doNothing() {
        subject.onResume()
        clearInvocations<AnimalRepository>(animalRepository)

        subject.onResume()

        verify(animalRepository, never()).fetchAnimals(any())
    }

    @Test
    fun requestPermissionToLoad_locationPermissionNotGranted_requestPermission() {
        whenever(permissionHelper.hasPermissions(ACCESS_FINE_LOCATION)).thenReturn(false)

        subject.requestPermissionToLoad()

        verify(eventBus) += check {
            assertThat((it as PermissionEvent).requestCode).isEqualTo(100)
            assertThat(it.permissions[0]).isEqualTo(ACCESS_FINE_LOCATION)
        }
    }

    @Test
    fun requestPermissionToLoad_locationPermissionGranted_usesAnimalTypeFromDataStoreAndClearsListAndStartsRefreshing() {
        whenever(animalRepository.fetchAnimals(any())).thenReturn(Observable.never())

        subject.requestPermissionToLoad()

        verify(animalRepository).fetchAnimals(check {
            assertThat(it.location).isEqualTo("zipcode")
            assertThat(it.animalType).isEqualTo(AnimalType.CAT)
            assertThat(it.lastOffset).isEqualTo(0)
        })
        verify(dataStore) += DiscoverErrorUseCase(null)
        verify(listAdapter).clearAnimalItems()
        assertThat(subject.refreshing.get()).isTrue()
    }

    @Test
    fun requestPermissionToLoad_getCurrentLocation_fetchesLocationAndSetsLocationChip() {
        whenever(animalRepository.fetchAnimals(any())).thenReturn(Observable.never())

        subject.requestPermissionToLoad()

        verify(locationHelper).fetchCurrentLocation()
        assertThat(subject.locationChip.get()).isNotNull()
        assertThat(subject.locationChip.get()!!.text).isEqualTo("Near zipcode")
    }

    @Test
    fun requestPermissionToLoad_getCurrentLocationASecondTime_resetsLocationChip() {
        whenever(animalRepository.fetchAnimals(any())).thenReturn(Observable.never())
        subject.requestPermissionToLoad()
        val newAddress: Address = mock()
        whenever(newAddress.postalCode).thenReturn("zipcode2")
        whenever(locationHelper.fetchCurrentLocation()).thenReturn(Observable.just(newAddress))

        subject.requestPermissionToLoad()

        assertThat(subject.locationChip.get()).isNotNull()
        assertThat(subject.locationChip.get()!!.text).isEqualTo("Near zipcode2")
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndFilterIsEmpty_doNotAddChips() {
        whenever(animalRepository.fetchAnimals(any())).thenReturn(Observable.never())

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(0)
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndSexIsNotMissing_addChip() {
        whenever(filter.sex).thenReturn(Sex.MALE)

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(1)
        assertThat(subject.chipList[0].text).isEqualTo("Male")
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndAgeIsNotMissing_addChip() {
        whenever(filter.age).thenReturn(Age.ADULT)

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(1)
        assertThat(subject.chipList[0].text).isEqualTo("Adult")
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndSizeIsNotMissing_addChip() {
        whenever(filter.size).thenReturn(AnimalSize.LARGE)

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(1)
        assertThat(subject.chipList[0].text).isEqualTo("Large")
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndBreedIsNotMissing_addChip() {
        whenever(filter.breed).thenReturn("Calico")

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(1)
        assertThat(subject.chipList[0].text).isEqualTo("Calico")
    }

    @Test
    fun requestPermissionToLoad_fetchAnimalsOnNext_sendAnimalListToAdapterAndStopsRefreshingAndNoErrors() {
        val animal: Animal = mock()
        val animalList = listOf(animal)
        whenever(animalListResponse.animalList).thenReturn(animalList)

        subject.requestPermissionToLoad()

        verify(animalListItemFactory).newInstance(animal)
        assertThat(subject.refreshing.get()).isFalse()
        verify(listAdapter).animalItems = animalList.map { animalListItemFactory.newInstance(it) } as ArrayList<AnimalListItemViewModel>
        verify(dataStore, times(2)) += DiscoverErrorUseCase(null)
    }

    @Test
    fun requestPermissionToLoad_fetchAnimalsOnError_sendsErrorToDataStore() {
        whenever(animalRepository.fetchAnimals(any())).thenReturn(Observable.error(PetfinderException(StatusCode.ERR_NO_ANIMALS)))

        subject.requestPermissionToLoad()

        assertThat(subject.refreshing.get()).isFalse()
        verify(dataStore) += DiscoverErrorUseCase(StatusCode.ERR_NO_ANIMALS)
    }

    @Test
    fun loadMoreAnimals_fetchAnimalsAtCurrentOffsetFetchesLocation() {
        val animal: Animal = mock()
        whenever(animalListResponse.lastOffset).thenReturn(10)
        whenever(animalListResponse.animalList).thenReturn(listOf(animal))
        subject.requestPermissionToLoad()
        verify(animalRepository).fetchAnimals(check { })
        clearInvocations<AnimalRepository>(animalRepository)
        clearInvocations<LocationHelper>(locationHelper)

        subject.loadMoreAnimals()

        verify(animalRepository).fetchAnimals(check {
            assertThat(it.lastOffset).isEqualTo(10)
        })
        verify(locationHelper).fetchCurrentLocation()
    }
}