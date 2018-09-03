package tech.pauly.findapet.favorites

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.data.AnimalRepository
import tech.pauly.findapet.data.FavoriteRepository
import tech.pauly.findapet.data.models.*
import tech.pauly.findapet.discover.AnimalListAdapter
import tech.pauly.findapet.discover.AnimalListItemViewModel
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus

class FavoritesViewModelTest {

    @RelaxedMockK
    private lateinit var listAdapter: AnimalListAdapter

    @MockK
    private lateinit var animalListItemFactory: AnimalListItemViewModel.Factory

    @RelaxedMockK
    private lateinit var dataStore: TransientDataStore

    @RelaxedMockK
    private lateinit var eventBus: ViewEventBus

    @MockK
    private lateinit var favoriteRepository: FavoriteRepository

    @MockK
    private lateinit var animalRepository: AnimalRepository

    @MockK
    private lateinit var favorite1: LocalAnimal

    @MockK
    private lateinit var favorite2: LocalAnimal

    @MockK
    private lateinit var favorite3: LocalAnimal

    @MockK
    private lateinit var favoriteVm1: AnimalListItemViewModel

    @MockK
    private lateinit var favoriteVm2: AnimalListItemViewModel

    @MockK
    private lateinit var favoriteVm3: AnimalListItemViewModel

    private lateinit var subject: FavoritesViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { favorite1.id } returns 1
        every { favorite2.id } returns 2
        every { favorite3.id } returns 3
        every { favoriteRepository.getFavoritedAnimals() } returns Observable.just(listOf(favorite1, favorite2))

        subject = FavoritesViewModel(listAdapter, animalListItemFactory, dataStore, eventBus, favoriteRepository, animalRepository)
    }

    @Test
    fun updateToolbar_updatesToolbarTitleAndOptionsMenu() {
        subject.updateToolbar()

        verify {
            dataStore += DiscoverToolbarTitleUseCase(R.string.menu_favorites)
            eventBus += OptionsMenuEvent(subject, OptionsMenuState.EMPTY)
        }
    }

    @Test
    fun loadFavorites_clearItemsSetsRefreshingAndGetFavoritedAnimals() {
        every { favoriteRepository.getFavoritedAnimals() } returns Observable.never()

        subject.loadFavorites()

        assertThat(subject.refreshing.get()).isTrue()
        verify {
            listAdapter.clearAnimalItems()
            favoriteRepository.getFavoritedAnimals()
        }
    }

    @Test
    fun loadFavorites_getsFavoritedAnimals_showAllAnimalsAndFetchEachAnimalFromNetwork() {
        every { animalListItemFactory.newInstance(favorite1) } returns favoriteVm1
        every { animalListItemFactory.newInstance(favorite2) } returns favoriteVm2
        every { animalRepository.fetchAnimal(any()) } returns Observable.never()

        subject.loadFavorites()

        verify {
            listAdapter.addAnimalItem(favoriteVm1)
            listAdapter.addAnimalItem(favoriteVm2)
            animalRepository.fetchAnimal(1)
            animalRepository.fetchAnimal(2)
        }
    }

    @Test
    fun loadFavorites_fetchAnimalsFromNetwork_showAllWarningsForUnauthorizedAnimals() {
        val status = mockk<Status> {
            every { code } returns StatusCode.PFAPI_ERR_UNAUTHORIZED
        }
        val header = mockk<Header> {
            every { this@mockk.status } returns status
        }
        val errorResponse = mockk<SingleAnimalResponse> {
            every { this@mockk.header } returns header
        }
        val successResponse = mockk<SingleAnimalResponse> {
            every { this@mockk.header } returns mockk(relaxed = true)
            every { this@mockk.animal } returns mockk()
        }
        every { animalListItemFactory.newInstance(favorite1) } returns favoriteVm1
        every { animalListItemFactory.newInstance(favorite2) } returns favoriteVm2
        every { animalListItemFactory.newInstance(favorite3) } returns favoriteVm3
        every { animalRepository.fetchAnimal(1) } returns Observable.just(SingleAnimalResponseWrapper(errorResponse, 1))
        every { animalRepository.fetchAnimal(2) } returns Observable.just(SingleAnimalResponseWrapper(successResponse, 2))
        every { animalRepository.fetchAnimal(3) } returns Observable.just(SingleAnimalResponseWrapper(errorResponse, 3))
        every { favoriteRepository.getFavoritedAnimals() } returns Observable.just(listOf(favorite1, favorite2, favorite3))

        subject.loadFavorites()

        verify {
            listAdapter.markAnimalWarning(1)
            listAdapter.markAnimalWarning(3)
        }
        verify(exactly = 0) {
            listAdapter.markAnimalWarning(2)
        }
    }
}