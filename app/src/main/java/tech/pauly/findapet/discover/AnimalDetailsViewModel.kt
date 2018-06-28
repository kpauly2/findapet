package tech.pauly.findapet.discover

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import tech.pauly.findapet.R
import tech.pauly.findapet.data.FavoriteRepository
import tech.pauly.findapet.data.models.*
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.AnimalDetailsUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.SnackbarEvent
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class AnimalDetailsViewModel @Inject
internal constructor(dataStore: TransientDataStore,
                     val detailsPagerAdapter: AnimalDetailsViewPagerAdapter,
                     val imagesPagerAdapter: AnimalImagesPagerAdapter,
                     private val resourceProvider: ResourceProvider,
                     private val favoriteRepository: FavoriteRepository,
                     private val eventBus: ViewEventBus) : BaseViewModel() {

    var name = ObservableField("")
    var age = ObservableInt(R.string.missing)
    var breeds = ObservableField("")
    var sex = ObservableInt(R.string.missing)
    var size = ObservableInt(R.string.missing)
    var options = ObservableField("")
    var description = ObservableField("")
    var descriptionVisibility = ObservableBoolean(false)
    var optionsVisibility = ObservableBoolean(false)
    var imagesPageLimit = ObservableInt(4)
    var imagesCount = ObservableInt(0)
    var currentImagePosition = ObservableInt(0)

    private var animal: Animal? = null

    init {
        detailsPagerAdapter.setViewModel(this)

        dataStore[AnimalDetailsUseCase::class]?.let {
            val animal = it.animal
            name.set(animal.name)
            sex.set(animal.sex.formattedName)
            size.set(animal.size.formattedName)
            age.set(animal.age.formattedName)
            breeds.set(animal.formattedBreedList)

            setPhotos(animal.photoUrlList)
            setOptions(animal.options)
            setDescription(animal.description)
            this.animal = animal
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun checkFavorite() {
        animal?.let {
            favoriteRepository.isAnimalFavorited(it.id)
                    .subscribe({ showFavoriteInOptions(it) }, Throwable::printStackTrace)
                    .onLifecycle()
        }
    }

    private fun showFavoriteInOptions(favorited: Boolean) {
        eventBus += OptionsMenuEvent(this, if (favorited) OptionsMenuState.FAVORITE else OptionsMenuState.NOT_FAVORITE)
    }

    private fun showFavoriteSnackbar(favorited: Boolean) {
        eventBus += SnackbarEvent(this, if (favorited) R.string.favorite_snackbar_message else R.string.unfavorite_snackbar_message)
    }

    fun imagePageChange(position: Int) {
        currentImagePosition.set(position)
    }

    internal fun changeFavorite(favorite: Boolean) {
        animal?.let {
            showFavoriteInOptions(favorite)
            val update = if (favorite) {
                favoriteRepository.favoriteAnimal(it)
            } else {
                favoriteRepository.unfavoriteAnimal(it)
            }
            update.subscribe({ showFavoriteSnackbar(favorite) }, Throwable::printStackTrace)
                    .onLifecycle()
        }
    }

    private fun setDescription(description: String?) {
        if (!description.isNullOrBlank()) {
            descriptionVisibility.set(true)
            this.description.set(description)
        }
    }

    private fun setOptions(options: List<Option>) {
        if (options.isNotEmpty()) {
            optionsVisibility.set(true)
            this.options.set(options.joinToString("\n") {
                resourceProvider.getString(it.formattedName)
            })
        }
    }

    private fun setPhotos(photoList: List<String>?) {
        photoList?.map {
            AnimalImageViewModel(it)
        }?.also {
            imagesCount.set(it.size)
            imagesPagerAdapter.setAnimalImages(it)
        }
    }
}
