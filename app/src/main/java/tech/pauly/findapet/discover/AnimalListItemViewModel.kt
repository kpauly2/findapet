package tech.pauly.findapet.discover

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import tech.pauly.findapet.data.models.Animal
import tech.pauly.findapet.data.models.Contact
import tech.pauly.findapet.data.models.Media
import tech.pauly.findapet.data.models.PhotoSize
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.AnimalDetailsUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class AnimalListItemViewModel(private val animal: Animal,
                              private val eventBus: ViewEventBus,
                              private val dataStore: TransientDataStore,
                              resourceProvider: ResourceProvider,
                              private val locationHelper: LocationHelper) : BaseViewModel() {

    var name = ObservableField("")
    var imageUrl = ObservableField("")
    var age = ObservableField("")
    var breeds = ObservableField("")
    var distance = ObservableField("?")
    var distanceVisibility = ObservableBoolean(false)

    init {
        name.set(animal.name)
        age.set(resourceProvider.getString(animal.age.formattedName))
        breeds.set(animal.formattedBreedList)

        setPhoto(animal.media)
        setDistance(animal.contact)
    }

    fun launchAnimalDetails() {
        dataStore += AnimalDetailsUseCase(animal)
        eventBus.send(ActivityEvent(this, AnimalDetailsActivity::class.java, false))
    }

    private fun setDistance(contactInfo: Contact) {
        locationHelper.getCurrentDistanceToContactInfo(contactInfo)
                .subscribe(this::displayDistance, Throwable::printStackTrace)
                .onLifecycle()
    }

    private fun displayDistance(returnDistance: Int) {
        distanceVisibility.set(returnDistance != -1)
        distance.set(if (returnDistance == 0) "< 1" else returnDistance.toString())
    }

    private fun setPhoto(media: Media?) {
        media?.photoList
                ?.find { it.size == PhotoSize.LARGE }
                ?.also { imageUrl.set(it.url) }
    }

    open class Factory @Inject
    constructor(private val eventBus: ViewEventBus,
                private val dataStore: TransientDataStore,
                private val resourceProvider: ResourceProvider,
                private val locationHelper: LocationHelper) {

        open fun newInstance(animal: Animal): AnimalListItemViewModel {
            return AnimalListItemViewModel(animal, eventBus, dataStore, resourceProvider, locationHelper)
        }
    }
}
