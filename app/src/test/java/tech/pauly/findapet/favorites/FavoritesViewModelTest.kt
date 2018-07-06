package tech.pauly.findapet.favorites

import com.nhaarman.mockito_kotlin.*
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

    private val listAdapter: AnimalListAdapter = mock()
    private val animalListItemFactory: AnimalListItemViewModel.Factory = mock()
    private val dataStore: TransientDataStore = mock()
    private val eventBus: ViewEventBus = mock()
    private val favoriteRepository: FavoriteRepository = mock()
    private val animalRepository: AnimalRepository = mock()

    private var favorite1: LocalAnimal = mock {
        on { id }.thenReturn(1)
    }
    private var favorite2: LocalAnimal = mock {
        on { id }.thenReturn(2)
    }
    private var favorite3: LocalAnimal = mock {
        on { id }.thenReturn(3)
    }
    private val favoriteVm1: AnimalListItemViewModel = mock()
    private val favoriteVm2: AnimalListItemViewModel = mock()
    private val favoriteVm3: AnimalListItemViewModel = mock()
    private lateinit var subject: FavoritesViewModel

    @Before
    fun setup() {
        whenever(favoriteRepository.getFavoritedAnimals()).thenReturn(Observable.just(listOf(favorite1, favorite2)))

        subject = FavoritesViewModel(listAdapter, animalListItemFactory, dataStore, eventBus, favoriteRepository, animalRepository)
    }

    @Test
    fun updateToolbar_updatesToolbarTitleAndOptionsMenu() {
        subject.updateToolbar()

        verify(dataStore) += DiscoverToolbarTitleUseCase(R.string.menu_favorites)
        verify(eventBus) += OptionsMenuEvent(subject, OptionsMenuState.EMPTY)
    }

    @Test
    fun loadFavorites_clearItemsSetsRefreshingAndGetFavoritedAnimals() {
        whenever(favoriteRepository.getFavoritedAnimals()).thenReturn(Observable.never())

        subject.loadFavorites()

        assertThat(subject.refreshing.get()).isTrue()
        verify(listAdapter).clearAnimalItems()
        verify(favoriteRepository).getFavoritedAnimals()
    }

    @Test
    fun loadFavorites_getsFavoritedAnimals_showAllAnimalsAndFetchEachAnimalFromNetwork() {
        whenever(animalListItemFactory.newInstance(favorite1)).thenReturn(favoriteVm1)
        whenever(animalListItemFactory.newInstance(favorite2)).thenReturn(favoriteVm2)
        whenever(animalRepository.fetchAnimal(any())).thenReturn(Observable.never())

        subject.loadFavorites()

        verify(listAdapter).addAnimalItem(favoriteVm1)
        verify(listAdapter).addAnimalItem(favoriteVm2)
        verify(animalRepository).fetchAnimal(1)
        verify(animalRepository).fetchAnimal(2)
    }

    @Test
    fun loadFavorites_fetchAnimalsFromNetwork_showAllWarningsForUnauthorizedAnimals() {
        val status: Status = mock {
            on { code }.thenReturn(StatusCode.PFAPI_ERR_UNAUTHORIZED)
        }
        val header: Header = mock { header ->
            on { header.status }.thenReturn(status)
        }
        val errorResponse: SingleAnimalResponse = mock { response ->
            on { response.header }.thenReturn(header)
        }
        val successResponse: SingleAnimalResponse = mock { response ->
            on { response.header }.thenReturn(mock())
            on { response.animal }.thenReturn(mock())
        }
        whenever(animalListItemFactory.newInstance(favorite1)).thenReturn(favoriteVm1)
        whenever(animalListItemFactory.newInstance(favorite2)).thenReturn(favoriteVm2)
        whenever(animalListItemFactory.newInstance(favorite3)).thenReturn(favoriteVm3)
        whenever(animalRepository.fetchAnimal(1)).thenReturn(Observable.just(SingleAnimalResponseWrapper(errorResponse, 1)))
        whenever(animalRepository.fetchAnimal(2)).thenReturn(Observable.just(SingleAnimalResponseWrapper(successResponse, 2)))
        whenever(animalRepository.fetchAnimal(3)).thenReturn(Observable.just(SingleAnimalResponseWrapper(errorResponse, 3)))
        whenever(favoriteRepository.getFavoritedAnimals()).thenReturn(Observable.just(listOf(favorite1, favorite2, favorite3)))

        subject.loadFavorites()

        verify(listAdapter).markAnimalWarning(1)
        verify(listAdapter, never()).markAnimalWarning(2)
        verify(listAdapter).markAnimalWarning(3)
    }
}