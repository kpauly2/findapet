package tech.pauly.findapet.discover

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import tech.pauly.findapet.data.models.*
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.LocationHelper
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.AnimalDetailsUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

open class AnimalListItemViewModel(private val animal: Animal,
                              private val eventBus: ViewEventBus,
                              private val dataStore: TransientDataStore,
                              resourceProvider: ResourceProvider) : BaseViewModel() {

    open var id: Int = 0
    var name = ObservableField("")
    var imageUrl = ObservableField("")
    var age = ObservableField("")
    var breeds = ObservableField("")
    var distanceVisibility = ObservableBoolean(false)
    var warning = ObservableBoolean(false)

    init {
        animal.also {
            id = it.id
            name.set(it.name)
            age.set(resourceProvider.getString(it.age.formattedName))
            breeds.set(it.formattedBreedList)
            imageUrl.set(animal.primaryPhotoUrl)
        }
    }

    fun launchAnimalDetails() {
        dataStore += AnimalDetailsUseCase(animal)
        eventBus += ActivityEvent(this, AnimalDetailsActivity::class, false)
    }

    open class Factory @Inject
    constructor(private val eventBus: ViewEventBus,
                private val dataStore: TransientDataStore,
                private val resourceProvider: ResourceProvider) {

        open fun newInstance(animal: Animal): AnimalListItemViewModel {
            return AnimalListItemViewModel(animal, eventBus, dataStore, resourceProvider)
        }
    }
}
