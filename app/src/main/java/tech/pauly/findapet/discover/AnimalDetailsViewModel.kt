package tech.pauly.findapet.discover

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import tech.pauly.findapet.R
import tech.pauly.findapet.data.FavoriteRepository
import tech.pauly.findapet.data.ShelterRepository
import tech.pauly.findapet.data.models.Animal
import tech.pauly.findapet.data.models.Option
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.AnimalDetailsUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.OptionsMenuEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.SnackbarEvent
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class AnimalDetailsViewModel @Inject
internal constructor(val detailsPagerAdapter: AnimalDetailsViewPagerAdapter,
                     val imagesPagerAdapter: AnimalImagesPagerAdapter,
                     private val dataStore: TransientDataStore,
                     private val resourceProvider: ResourceProvider,
                     private val favoriteRepository: FavoriteRepository,
                     private val eventBus: ViewEventBus,
                     private val shelterRepository: ShelterRepository,
                     private val locationHelper: LocationHelper) : BaseViewModel() {

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
    var contactName = ObservableField("")
    var contactAddress = ObservableField("")
    var contactPhone = ObservableField("")
    var contactEmail = ObservableField("")
    var contactDistance = ObservableField("")
    var contactPhoneVisibility = ObservableBoolean(false)
    var contactEmailVisibility = ObservableBoolean(false)
    var partialContact = ObservableBoolean(false)

    private var animal: Animal? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setupPage() {
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
            updateShelter(animal)
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

    fun imagePageChange(position: Int) {
        currentImagePosition.set(position)
    }

    fun clickAddress(v: View) {
        //TODO: https://www.pivotaltracker.com/story/show/157158656
    }

    fun clickPhone(v: View) {
        //TODO: https://www.pivotaltracker.com/story/show/157158656
    }

    fun clickEmail(v: View) {
        //TODO: https://www.pivotaltracker.com/story/show/157158656
    }

    private fun showFavoriteInOptions(favorited: Boolean) {
        eventBus += OptionsMenuEvent(this, if (favorited) OptionsMenuState.FAVORITE else OptionsMenuState.NOT_FAVORITE)
    }

    private fun showFavoriteSnackbar(favorited: Boolean) {
        eventBus += SnackbarEvent(this, if (favorited) R.string.favorite_snackbar_message else R.string.unfavorite_snackbar_message)
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

    private fun updateShelter(animal: Animal) {
        shelterRepository.fetchShelter(animal)
                .flatMap {
                    this.partialContact.set(it.name == null)
                    contactName.set(it.name)
                    contactAddress.set(it.formattedAddress)
                    contactPhoneVisibility.set(it.phone != null)
                    contactPhone.set(it.phone)
                    contactEmailVisibility.set(it.email != null)
                    contactEmail.set(it.email)
                    locationHelper.getCurrentDistanceToContactInfo(it)
                }.subscribe({
                    contactDistance.set(when (it) {
                        -1 -> resourceProvider.getString(R.string.empty_string)
                        0 -> resourceProvider.getString(R.string.distance_less_than_one)
                        else -> resourceProvider.getQuantityString(R.plurals.distance, it)
                    })
                }, Throwable::printStackTrace)
    }
}
