package tech.pauly.findapet.discover

import androidx.databinding.Bindable
import android.view.View
import io.reactivex.subjects.PublishSubject
import tech.pauly.findapet.BR
import tech.pauly.findapet.R
import tech.pauly.findapet.data.BreedRepository
import tech.pauly.findapet.data.models.BreedListResponse
import tech.pauly.findapet.shared.BaseViewModel
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.FilterAnimalTypeUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.utils.ObservableString
import tech.pauly.findapet.utils.isToggleChecked
import javax.inject.Inject

class BreedViewModel @Inject
internal constructor(private val breedRepository: BreedRepository,
                     private val dataStore: TransientDataStore,
                     private val resourceProvider: ResourceProvider,
                     val adapter: BreedAdapter) : BaseViewModel() {

    var breedSearchText = ObservableString()
    val openBreedSubject = PublishSubject.create<Boolean>()
    var selectedBreed: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.displayBreed)
        }

    private var searching = false
    private var masterBreedList: List<String> = ArrayList()
        get() = field.sortedBy { it != (selectedBreed ?: "") }

    init {
        adapter.viewModel = this
    }

    override fun onBackPressed(): Boolean {
        return when {
            searching -> { closeBreedSearch(); false }
            else -> true
        }
    }

    @Bindable
    fun getDisplayBreed(): String {
        return when {
            selectedBreed.isNullOrBlank() -> resourceProvider.getString(R.string.filter_breed_hint)
            else -> selectedBreed!!
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun beginSearch(v: View) {
        searching = true
        openBreedSubject.onNext(true)
        breedSearchText.set("")
        dataStore[FilterAnimalTypeUseCase::class]?.let(this::updateBreedList)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onBreedTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        val filteredBreedList = masterBreedList.filter {
            it.toLowerCase().contains(text.toString().toLowerCase())
        }
        adapter.addBreeds(filteredBreedList)
    }

    fun selectBreed(view: View, breed: String) {
        selectedBreed = if (view.isToggleChecked) breed else ""
        adapter.addBreeds(masterBreedList)
        closeBreedSearch()
    }

    fun closeBreedSearch() {
        searching = false
        openBreedSubject.onNext(false)
    }

    private fun updateBreedList(useCase: FilterAnimalTypeUseCase) {
        breedRepository.getBreedList(useCase.animalType)
                .quickSubscribe(this::populateBreedList)
    }

    private fun populateBreedList(response: BreedListResponse) {
        response.breedList?.let {
            masterBreedList = it
            adapter.addBreeds(masterBreedList)
        }
    }
}