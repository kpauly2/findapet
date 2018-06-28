package tech.pauly.findapet.favorites

import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.data.AnimalRepository
import tech.pauly.findapet.data.FavoriteRepository
import tech.pauly.findapet.data.models.LocalAnimal
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

    private var favorite1: LocalAnimal = mock()
    private var favorite2: LocalAnimal = mock()
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
    fun loadFavorites_addAnimalItemForEachAnimalThenSetRefreshingFalse() {
        val favoriteVm1: AnimalListItemViewModel = mock()
        val favoriteVm2: AnimalListItemViewModel = mock()
        whenever(animalListItemFactory.newInstance(favorite1)).thenReturn(favoriteVm1)
        whenever(animalListItemFactory.newInstance(favorite2)).thenReturn(favoriteVm2)

        subject.loadFavorites()

        verify(listAdapter).addAnimalItem(favoriteVm1)
        verify(listAdapter).addAnimalItem(favoriteVm2)
        assertThat(subject.refreshing.get()).isFalse()
    }
}