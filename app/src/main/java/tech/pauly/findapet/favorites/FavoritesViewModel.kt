package tech.pauly.findapet.favorites

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableBoolean
import android.databinding.ObservableInt
import tech.pauly.findapet.R
import tech.pauly.findapet.data.AnimalRepository
import tech.pauly.findapet.data.FavoriteRepository
import tech.pauly.findapet.data.PetfinderException
import tech.pauly.findapet.data.models.LocalAnimal
import tech.pauly.findapet.data.models.StatusCode
import tech.pauly.findapet.discover.AnimalListAdapter
import tech.pauly.findapet.discover.AnimalListItemViewModel
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class FavoritesViewModel @Inject
internal constructor(val listAdapter: AnimalListAdapter,
                     private val animalListItemFactory: AnimalListItemViewModel.Factory,
                     private val dataStore: TransientDataStore,
                     private val eventBus: ViewEventBus,
                     private val favoriteRepository: FavoriteRepository,
                     private val animalRepository: AnimalRepository) : BaseViewModel() {

    var columnCount = ObservableInt(2)
    var refreshing = ObservableBoolean(false)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbar() {
        dataStore += DiscoverToolbarTitleUseCase(R.string.menu_favorites)
        eventBus += OptionsMenuEvent(this, OptionsMenuState.EMPTY)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun loadFavorites() {
        refreshing.set(true)
        listAdapter.clearAnimalItems()
        favoriteRepository.getFavoritedAnimals()
                .flatMapIterable { it }
                .subscribe(this::showAnimal, this::animalFetchFailure) {
                    refreshing.set(false)
                }
                .onLifecycle()
    }

    private fun showAnimal(animal: LocalAnimal) {
        listAdapter.addAnimalItem(animalListItemFactory.newInstance(animal))
    }

    private fun animalFetchFailure(throwable: Throwable) {
        if ((throwable as? PetfinderException)?.statusCode == StatusCode.PFAPI_ERR_NOENT) {
            // TODO: show adopted messages: https://www.pivotaltracker.com/story/show/157159241
        } else {
            throwable.printStackTrace()
        }
    }
}
