package tech.pauly.old.discover

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import android.view.View
import tech.pauly.old.R
import tech.pauly.old.data.models.Animal
import tech.pauly.old.data.models.AnimalUrl
import tech.pauly.old.data.models.Sex
import tech.pauly.old.shared.BaseViewModel
import tech.pauly.old.shared.ResourceProvider
import tech.pauly.old.shared.SentencePlacement
import tech.pauly.old.shared.datastore.AnimalDetailsUseCase
import tech.pauly.old.shared.datastore.AnimalDetailsUseCase.Tab
import tech.pauly.old.shared.datastore.TransientDataStore
import tech.pauly.old.shared.events.ActivityEvent
import tech.pauly.old.shared.events.DialogEvent
import tech.pauly.old.shared.events.ViewEventBus
import tech.pauly.old.utils.ObservableString
import tech.pauly.old.utils.safeGet
import javax.inject.Inject

open class AnimalListItemViewModel(private val animal: Animal,
                                   private val eventBus: ViewEventBus,
                                   private val dataStore: TransientDataStore,
                                   private val resourceProvider: ResourceProvider) : BaseViewModel() {
    open var id: Int = 0
    var name = ObservableString()
    var imageUrl = ObservableField<AnimalUrl>()
    var age = ObservableString()
    var breeds = ObservableString()
    var distanceVisibility = ObservableBoolean(false)
    var warning = ObservableBoolean(false)

    private var sex = Sex.MISSING

    init {
        animal.also {
            id = it.id
            name.set(it.name)
            age.set(resourceProvider.getString(it.age.formattedName))
            breeds.set(it.formattedBreedList)
            imageUrl.set(animal.primaryPhotoUrl)
            warning.set(it.warning)
            sex = it.sex
        }
    }

    fun launchAnimalDetails(tab: Tab) {
        dataStore += AnimalDetailsUseCase(animal, tab)
        eventBus += ActivityEvent(this, AnimalDetailsActivity::class, false)
    }

    @Suppress("UNUSED_PARAMETER")
    fun showPetWarningDialog(v: View) {
        if (!warning.get()) {
            launchAnimalDetails(Tab.DETAILS)
            return
        }
        val bodyText = resourceProvider.getSexString(R.string.pet_warning_dialog_body,
                sex,
                name.safeGet(),
                SentencePlacement.OBJECT,
                SentencePlacement.SUBJECT)
        eventBus += DialogEvent(this,
                R.string.pet_warning_dialog_title,
                bodyText,
                R.string.pet_warning_dialog_contact,
                R.string.dialog_cancel,
                { launchAnimalDetails(Tab.CONTACT) },
                null,
                imageUrl.get(),
                R.color.warning)
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
