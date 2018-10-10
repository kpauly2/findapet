package tech.pauly.findapet.discover

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableField
import android.view.View
import tech.pauly.findapet.data.FilterRepository
import tech.pauly.findapet.data.models.Age
import tech.pauly.findapet.data.models.AnimalSize
import tech.pauly.findapet.data.models.Filter
import tech.pauly.findapet.data.models.Sex
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.ViewEventBus
import tech.pauly.findapet.utils.isToggleChecked
import javax.inject.Inject

class FilterViewModel @Inject
internal constructor(private val filterRepository: FilterRepository,
                     private val eventBus: ViewEventBus,
                     private val dataStore: TransientDataStore,
                     val breedViewModel: BreedViewModel) : BaseViewModel() {

    var selectedSex = ObservableField(Sex.MISSING)
    var selectedAge = ObservableField(Age.MISSING)
    var selectedSize = ObservableField(AnimalSize.MISSING)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun loadCurrentFilter() {
        filterRepository.currentFilter
                .quickSubscribe(this::populateScreenForFilter)
    }

    fun checkToggle(view: View, choice: Any) {
        when (choice) {
            is Sex -> selectedSex.set(if (view.isToggleChecked) choice else Sex.MISSING)
            is Age -> selectedAge.set(if (view.isToggleChecked) choice else Age.MISSING)
            is AnimalSize -> selectedSize.set(if (view.isToggleChecked) choice else AnimalSize.MISSING)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun saveFilter(v: View) {
        val filter = Filter().apply {
            selectedSex.get()?.let { sex = it }
            selectedAge.get()?.let { age = it }
            selectedSize.get()?.let { size = it }
            breedViewModel.selectedBreed?.let { breed = it }
        }
        filterRepository.insertFilter(filter)
                .quickSubscribe(this::finish)
    }

    private fun finish() {
        dataStore += FilterUpdatedUseCase()
        eventBus += ActivityEvent(this, null, true)
    }

    private fun populateScreenForFilter(filter: Filter) {
        filter.apply {
            selectedSex.set(sex)
            selectedAge.set(age)
            selectedSize.set(size)
            breedViewModel.selectedBreed = breed
        }
    }
}