package tech.pauly.old.favorites

import androidx.lifecycle.OnLifecycleEvent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.Lifecycle
import tech.pauly.old.R
import tech.pauly.old.data.AnimalRepository
import tech.pauly.old.data.FavoriteRepository
import tech.pauly.old.data.models.Animal
import tech.pauly.old.data.models.AnimalResponseWrapper
import tech.pauly.old.data.models.StatusCode
import tech.pauly.old.discover.AnimalListAdapter
import tech.pauly.old.discover.AnimalListItemViewModel
import tech.pauly.old.shared.BaseViewModel
import tech.pauly.old.shared.ResourceProvider
import tech.pauly.old.shared.SentencePlacement
import tech.pauly.old.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.old.shared.datastore.TransientDataStore
import tech.pauly.old.shared.events.DialogEvent
import tech.pauly.old.shared.events.OptionsMenuEvent
import tech.pauly.old.shared.events.OptionsMenuState
import tech.pauly.old.shared.events.ViewEventBus
import tech.pauly.old.utils.Optional
import javax.inject.Inject

class FavoritesViewModel @Inject
internal constructor(val listAdapter: AnimalListAdapter,
                     private val animalListItemFactory: AnimalListItemViewModel.Factory,
                     private val dataStore: TransientDataStore,
                     private val eventBus: ViewEventBus,
                     private val favoriteRepository: FavoriteRepository,
                     private val animalRepository: AnimalRepository,
                     private val resourceProvider: ResourceProvider) : BaseViewModel() {

    var columnCount = ObservableInt(2)
    var refreshing = ObservableBoolean(false)
    private var animalAdoptedList = ArrayList<Animal>()

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
                .flatMap(animalRepository::fetchAnimal)
                .map(this::parseResponseAndShowError)
                .subscribe(this::showAnimal, Throwable::printStackTrace, this::finalizeLoad)
                .onLifecycle()
    }

    private fun parseResponseAndShowError(responseWrapper: AnimalResponseWrapper): Optional<Animal> {
        val statusCode = responseWrapper.header.status?.code ?: StatusCode.PFAPI_OK
        val animal = responseWrapper.animal
        return when (statusCode) {
            StatusCode.PFAPI_ERR_UNAUTHORIZED -> {
                Optional.Some(animal.apply { warning = true })
            }
            StatusCode.PFAPI_ERR_NOENT -> {
                animalAdoptedList.add(animal)
                Optional.None
            }
            else -> Optional.Some(animal)
        }
    }

    private fun showAnimal(optionalAnimal: Optional<Animal>) {
        if (optionalAnimal is Optional.Some) {
            listAdapter.addAnimalItem(animalListItemFactory.newInstance(optionalAnimal.element))
        }
    }

    private fun showAnimalAdoptedDialog(animal: Animal) {
        val bodyText = resourceProvider.getSexString(R.string.pet_adopted_dialog_body,
                animal.sex,
                animal.name,
                SentencePlacement.SUBJECT)
        eventBus += DialogEvent(this,
                R.string.pet_adopted_dialog_title,
                bodyText,
                R.string.pet_adopted_remove_button,
                null,
                { clickPositiveButton(animal) },
                null,
                animal.primaryPhotoUrl,
                blockBackgroundTouches = true)
    }

    private fun clickPositiveButton(animal: Animal) {
        favoriteRepository.unfavoriteAnimal(animal).quickSubscribe { }
        showNextAdoptedAnimal()
    }

    private fun finalizeLoad() {
        refreshing.set(false)
        showNextAdoptedAnimal()
    }

    private fun showNextAdoptedAnimal() {
        animalAdoptedList.getOrNull(0)?.let {
            showAnimalAdoptedDialog(it)
            animalAdoptedList.removeAt(0)
        }
    }
}
