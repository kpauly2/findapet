package tech.pauly.findapet.discover

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Address
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.reactivex.Observable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
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
import tech.pauly.findapet.shared.events.*
import java.util.*

class DiscoverViewModelTest {

    @RelaxedMockK
    private lateinit var listAdapter: AnimalListAdapter

    @RelaxedMockK
    private lateinit var animalListItemFactory: AnimalListItemViewModel.Factory

    @MockK
    private lateinit var animalRepository: AnimalRepository

    @RelaxedMockK
    private lateinit var dataStore: TransientDataStore

    @MockK
    private lateinit var permissionHelper: PermissionHelper

    @RelaxedMockK
    private lateinit var eventBus: ViewEventBus

    @RelaxedMockK
    private lateinit var locationHelper: LocationHelper

    @RelaxedMockK
    private lateinit var animalListResponse: AnimalListResponse

    @MockK
    private lateinit var resourceProvider: ResourceProvider

    @MockK
    private lateinit var filterRepository: FilterRepository

    @MockK
    private lateinit var fetchAnimalsRequest: FetchAnimalsRequest

    @MockK
    private lateinit var filter: Filter

    private lateinit var subject: DiscoverViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        resourceProvider.apply {
            every { getString(R.string.male) } returns "Male"
            every { getString(R.string.adult) } returns "Adult"
            every { getString(R.string.large) } returns "Large"
            every { getString(R.string.chip_near_location, "zipcode") } returns "Near zipcode"
            every { getString(R.string.chip_near_location, "zipcode2") } returns "Near zipcode2"
        }
        filter.apply {
            every { sex } returns Sex.MISSING
            every { age } returns Age.MISSING
            every { size } returns AnimalSize.MISSING
            every { breed } returns ""
        }
        val useCase = mockk<DiscoverAnimalTypeUseCase> {
            every { animalType } returns AnimalType.CAT
        }
        every { dataStore[DiscoverAnimalTypeUseCase::class] } returns useCase
        every { permissionHelper.hasPermissions(ACCESS_FINE_LOCATION) } returns true
        every { animalRepository.fetchAnimals(any()) } returns Observable.just(animalListResponse)
        val currLocation = mockk<Address> {
            every { postalCode } returns "zipcode"
        }
        every { locationHelper.fetchCurrentLocation() } returns Observable.just(currLocation)
        fetchAnimalsRequest.apply {
            every { animalType } returns AnimalType.CAT
            every { location } returns "zipcode"
            every { lastOffset } returns 0
            every { filter } returns this@DiscoverViewModelTest.filter
        }
        every { filterRepository.currentFilterAndNoFilterIfEmpty } returns Single.just(filter)
        every { dataStore[FilterUpdatedUseCase::class] } returns null

        subject = DiscoverViewModel(listAdapter, animalListItemFactory, animalRepository, dataStore, permissionHelper, eventBus, locationHelper, resourceProvider, filterRepository)
    }

    @Test
    fun updateToolbar_updatesToolbarTitleAndOptionsMenu() {
        subject.updateToolbar()

        verify {
            dataStore += DiscoverToolbarTitleUseCase(AnimalType.CAT.toolbarName)
            eventBus += OptionsMenuEvent(subject, OptionsMenuState.DISCOVER)
        }
    }

    @Test
    fun onResume_firstLoad_loadList() {
        subject.onResume()

        verify {
            animalRepository.fetchAnimals(any())
        }
    }

    @Test
    fun onResume_notFirstLoadButFilterUpdated_loadList() {
        subject.onResume()
        every { dataStore[FilterUpdatedUseCase::class] } returns mockk()

        subject.onResume()

        verify {
            animalRepository.fetchAnimals(any())
        }
    }

    @Test
    fun onResume_notFirstLoadAndFilterNotUpdated_doNothing() {
        subject.onResume()
        clearMocks(animalRepository)

        subject.onResume()

        verify(exactly = 0) {
            animalRepository.fetchAnimals(any())
        }
    }

    @Test
    fun requestPermissionToLoad_locationPermissionNotGranted_requestPermission() {
        every { permissionHelper.hasPermissions(ACCESS_FINE_LOCATION) } returns false
        val slot = slot<BaseViewEvent>()
        every { eventBus += capture(slot) } answers { nothing }

        subject.requestPermissionToLoad()

        slot.captured.also {
            assertThat((it as PermissionEvent).requestCode).isEqualTo(100)
            assertThat(it.permissions[0]).isEqualTo(ACCESS_FINE_LOCATION)
        }
    }

    @Test
    fun requestPermissionToLoad_locationPermissionGranted_usesAnimalTypeFromDataStoreAndClearsListAndStartsRefreshing() {
        val slot = slot<FetchAnimalsRequest>()
        every { animalRepository.fetchAnimals(capture(slot)) } returns Observable.never()

        subject.requestPermissionToLoad()

        assertThat(subject.refreshing.get()).isTrue()
        slot.captured.also {
            assertThat(it.location).isEqualTo("zipcode")
            assertThat(it.animalType).isEqualTo(AnimalType.CAT)
            assertThat(it.lastOffset).isEqualTo(0)
        }
        verify {
            dataStore += DiscoverErrorUseCase(null)
            listAdapter.clearAnimalItems()
        }
    }

    @Test
    fun requestPermissionToLoad_getCurrentLocation_fetchesLocationAndSetsLocationChip() {
        every { animalRepository.fetchAnimals(any()) } returns Observable.never()

        subject.requestPermissionToLoad()

        assertThat(subject.locationChip.get()).isNotNull()
        assertThat(subject.locationChip.get()!!.text).isEqualTo("Near zipcode")
        verify { locationHelper.fetchCurrentLocation() }
    }

    @Test
    fun requestPermissionToLoad_getCurrentLocationASecondTime_resetsLocationChip() {
        every { animalRepository.fetchAnimals(any()) } returns Observable.never()
        subject.requestPermissionToLoad()
        val currLocation = mockk<Address> {
            every { postalCode } returns "zipcode2"
        }
        every { locationHelper.fetchCurrentLocation() } returns Observable.just(currLocation)

        subject.requestPermissionToLoad()

        assertThat(subject.locationChip.get()).isNotNull()
        assertThat(subject.locationChip.get()!!.text).isEqualTo("Near zipcode2")
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndFilterIsEmpty_doNotAddChips() {
        every { animalRepository.fetchAnimals(any()) } returns Observable.never()

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(0)
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndSexIsNotMissing_addChip() {
        every { filter.sex } returns Sex.MALE

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(1)
        assertThat(subject.chipList[0].text).isEqualTo("Male")
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndAgeIsNotMissing_addChip() {
        every { filter.age } returns Age.ADULT

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(1)
        assertThat(subject.chipList[0].text).isEqualTo("Adult")
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndSizeIsNotMissing_addChip() {
        every { filter.size } returns AnimalSize.LARGE

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(1)
        assertThat(subject.chipList[0].text).isEqualTo("Large")
    }

    @Test
    fun requestPermissionToLoad_getCurrentFilterAndBreedIsNotMissing_addChip() {
        every { filter.breed } returns "Calico"

        subject.requestPermissionToLoad()

        assertThat(subject.chipList.size).isEqualTo(1)
        assertThat(subject.chipList[0].text).isEqualTo("Calico")
    }

    @Test
    fun requestPermissionToLoad_fetchAnimalsOnNext_sendAnimalListToAdapterAndStopsRefreshingAndNoErrors() {
        val animal: InternetAnimal = mockk()
        val animalList = listOf(animal)
        every { animalListResponse.animalList } returns animalList
        every { animalListResponse.lastOffset } returns 0
        val slot = slot<ArrayList<AnimalListItemViewModel>>()
        val viewModel = mockk<AnimalListItemViewModel>()
        every { animalListItemFactory.newInstance(animal) } returns viewModel
        every { listAdapter.animalItems = capture(slot) } answers { nothing }

        subject.requestPermissionToLoad()

        assertThat(subject.refreshing.get()).isFalse()
        assertThat(slot.captured[0]).isEqualTo(viewModel)
        verify {
            animalListItemFactory.newInstance(animal)
        }
        verify(exactly = 2) {
            dataStore += DiscoverErrorUseCase(null)
        }
    }

    @Test
    fun requestPermissionToLoad_fetchAnimalsOnError_sendsErrorToDataStore() {
        every { animalRepository.fetchAnimals(any()) } returns Observable.error(PetfinderException(StatusCode.ERR_NO_ANIMALS))

        subject.requestPermissionToLoad()

        assertThat(subject.refreshing.get()).isFalse()
        verify {
            dataStore += DiscoverErrorUseCase(StatusCode.ERR_NO_ANIMALS)
        }
    }

    @Test
    fun loadMoreAnimals_fetchAnimalsAtCurrentOffsetFetchesLocation() {
        every { animalListItemFactory.newInstance(any()) } returns mockk()
        val animal: InternetAnimal = mockk()
        every { animalListResponse.lastOffset } returns 10
        every { animalListResponse.animalList } returns listOf(animal)
        subject.requestPermissionToLoad()
        verify { animalRepository.fetchAnimals(any()) }
        val slot = slot<FetchAnimalsRequest>()
        every { animalRepository.fetchAnimals(capture(slot)) } returns Observable.never()

        subject.loadMoreAnimals()

        assertThat(slot.captured.lastOffset).isEqualTo(10)
        verify {
            locationHelper.fetchCurrentLocation()
        }
    }

    @Test
    fun loadMoreAnimals_getsSameAnimalsAgain_loadMoreAnimalsDoesNotFetchAgain() {
        every { animalListResponse.lastOffset } returns 10
        val internalAnimal = mockk<InternetAnimal> {
            every { id } returns 10
        }
        every { animalListResponse.animalList } returns listOf(internalAnimal)
        val viewModel = mockk<AnimalListItemViewModel> {
            every { id } returns 10
        }
        every { listAdapter.animalItems } returns arrayListOf(viewModel)
        subject.requestPermissionToLoad()
        clearMocks(animalRepository)

        subject.loadMoreAnimals()

        verify(exactly = 0) { animalRepository.fetchAnimals(any()) }
    }
}