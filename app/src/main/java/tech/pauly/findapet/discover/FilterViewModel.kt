package tech.pauly.findapet.discover

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableField
import android.view.View
import android.widget.ToggleButton

import java.util.ArrayList

import javax.inject.Inject

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import tech.pauly.findapet.data.BreedRepository
import tech.pauly.findapet.data.FilterRepository
import tech.pauly.findapet.data.models.Age
import tech.pauly.findapet.data.models.AnimalSize
import tech.pauly.findapet.data.models.BreedListResponse
import tech.pauly.findapet.data.models.Filter
import tech.pauly.findapet.data.models.Sex
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.datastore.FilterAnimalTypeUseCase
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.ViewEventBus

class FilterViewModel @Inject
internal constructor(private val filterRepository: FilterRepository,
                     private val breedRepository: BreedRepository,
                     private val eventBus: ViewEventBus,
                     private val dataStore: TransientDataStore,
                     val adapter: FilterAdapter) : BaseViewModel() {

    var selectedSex = ObservableField(Sex.MISSING)
    var selectedAge = ObservableField(Age.MISSING)
    var selectedSize = ObservableField(AnimalSize.MISSING)
    var selectedBreed = ObservableField("")
    var breedSearchText = ObservableField("")

    val scrollToViewSubject = PublishSubject.create<Boolean>()
    private var masterBreedList: List<String> = ArrayList()

    init {
        adapter.viewModel = this
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun loadCurrentFilter() {
        filterRepository.currentFilter
                .subscribe(this::populateScreenForFilter, Throwable::printStackTrace)
                .onLifecycle()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateBreedList() {
        dataStore[FilterAnimalTypeUseCase::class]?.let {
            breedRepository.getBreedList(it.animalType)
                    .subscribe(this::populateBreedList, Throwable::printStackTrace)
                    .onLifecycle()
        }
    }

    fun clickBreedSearch() {
        scrollToViewSubject.onNext(true)
    }

    fun onBreedTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        val filteredBreedList = masterBreedList.filter {
            it.toLowerCase().contains(text.toString().toLowerCase())
        }.sortedBy { it != selectedBreed.get() }
        adapter.setBreedItems(filteredBreedList)
        scrollToViewSubject.onNext(true)
    }

    fun checkSex(view: View, sex: Sex) {
        selectedSex.set(if (view.isToggleChecked) sex else Sex.MISSING)
    }

    fun checkAge(view: View, age: Age) {
        selectedAge.set(if (view.isToggleChecked) age else Age.MISSING)
    }

    fun checkSize(view: View, size: AnimalSize) {
        selectedSize.set(if (view.isToggleChecked) size else AnimalSize.MISSING)
    }

    fun checkBreed(view: View, breed: String) {
        selectedBreed.set(if (view.isToggleChecked) breed else "")
    }

    fun saveFilter(v: View) {
        val filter = Filter().apply {
            selectedSex.get()?.let { sex = it }
            selectedAge.get()?.let { age = it }
            selectedSize.get()?.let { size = it }
            selectedBreed.get()?.let { breed = it }
        }
        filterRepository.insertFilter(filter)
                .subscribe(this::finish, Throwable::printStackTrace)
                .onLifecycle()
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
            selectedBreed.set(breed)
        }
    }

    private fun populateBreedList(response: BreedListResponse) {
        response.breedList?.let {
            masterBreedList = it
            adapter.setBreedItems(it.sortedBy { it != selectedBreed.get() })
        }
    }

    private val View.isToggleChecked: Boolean
        get() = (this as? ToggleButton)?.isChecked == true
}
