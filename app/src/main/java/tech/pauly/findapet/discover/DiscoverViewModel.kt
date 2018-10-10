package tech.pauly.findapet.discover

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.*
import android.location.Address
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import tech.pauly.findapet.R
import tech.pauly.findapet.data.AnimalRepository
import tech.pauly.findapet.data.FilterRepository
import tech.pauly.findapet.data.PetfinderException
import tech.pauly.findapet.data.models.*
import tech.pauly.findapet.shared.*
import tech.pauly.findapet.shared.datastore.DiscoverAnimalTypeUseCase
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.FilterUpdatedUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.*
import tech.pauly.findapet.utils.Chip
import java.util.*
import javax.inject.Inject

class DiscoverViewModel @Inject
constructor(val listAdapter: AnimalListAdapter,
            private val animalListItemFactory: AnimalListItemViewModel.Factory,
            private val animalRepository: AnimalRepository,
            private val dataStore: TransientDataStore,
            private val permissionHelper: PermissionHelper,
            private val eventBus: ViewEventBus,
            private val locationHelper: LocationHelper,
            private val resourceProvider: ResourceProvider,
            private val filterRepository: FilterRepository) : BaseViewModel() {

    var columnCount = ObservableInt(2)
    var refreshing = ObservableBoolean(false)
    var chipList: ObservableList<Chip> = ObservableArrayList()
    var locationChip = ObservableField<Chip>()

    private var animalType = AnimalType.CAT
    private var lastOffset = 0
    private var shouldLoadMoreAnimals = true
    private var firstLoad = true

    private val currentFilter: Observable<Filter>
        get() = filterRepository.currentFilterAndNoFilterIfEmpty
                .doOnSubscribe(this::removeFilterChips)
                .doOnSuccess(this::addFilterChips).toObservable()

    private val currentLocation: Observable<Address>
        get() = locationHelper.fetchCurrentLocation()
                .doOnSubscribe(this::removeLocationChip)
                .doOnNext(this::addLocationChip)

    init {
        dataStore[DiscoverAnimalTypeUseCase::class]?.let {
            animalType = it.animalType
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateToolbar() {
        dataStore += DiscoverToolbarTitleUseCase(animalType.toolbarName)
        eventBus += OptionsMenuEvent(this, OptionsMenuState.DISCOVER)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        val filterUpdated = dataStore[FilterUpdatedUseCase::class] != null
        if (firstLoad || filterUpdated) {
            firstLoad = false
            requestPermissionToLoad()
        }
    }

    fun requestPermissionToLoad() {
        if (permissionHelper.hasPermissions(ACCESS_FINE_LOCATION)) {
            loadList()
        } else {
            eventBus += PermissionEvent(this,
                    arrayOf(ACCESS_FINE_LOCATION),
                    locationPermissionResponseListener(),
                    100)
        }
    }

    private fun loadList() {
        listAdapter.clearAnimalItems()
        lastOffset = 0
        shouldLoadMoreAnimals = true
        fetchAnimals()
    }

    fun loadMoreAnimals() {
        if (shouldLoadMoreAnimals) {
            fetchAnimals()
        }
    }

    private fun fetchAnimals() {
        refreshing.set(true)
        dataStore += DiscoverErrorUseCase(null)
        Observable.zip<Address, Filter, FetchAnimalsRequest>(currentLocation,
                currentFilter, BiFunction { location, filter -> FetchAnimalsRequest(animalType, lastOffset, location.postalCode, filter) })
                .flatMap(animalRepository::fetchAnimals)
                .subscribe(this::setAnimalList, this::showError)
                .onLifecycle()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun removeFilterChips(disposable: Disposable) {
        chipList.clear()
    }

    private fun addFilterChips(filter: Filter) {
        filter.apply {
            if (sex !== Sex.MISSING) {
                chipList.add(Chip(resourceProvider.getString(sex.formattedName)))
            }
            if (age !== Age.MISSING) {
                chipList.add(Chip(resourceProvider.getString(age.formattedName)))
            }
            if (size !== AnimalSize.MISSING) {
                chipList.add(Chip(resourceProvider.getString(size.formattedName)))
            }
            if (breed != "") {
                chipList.add(Chip(filter.breed))
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun removeLocationChip(disposable: Disposable) {
        locationChip.set(null)
    }

    private fun addLocationChip(location: Address) {
        locationChip.set(Chip(resourceProvider.getString(R.string.chip_near_location, location.postalCode)))
    }

    private fun showError(throwable: Throwable) {
        refreshing.set(false)
        if (throwable is PetfinderException) {
            dataStore += DiscoverErrorUseCase(throwable.statusCode)
        }
        throwable.printStackTrace()
    }

    private fun setAnimalList(animalListResponse: AnimalListResponse) {
        refreshing.set(false)
        dataStore += DiscoverErrorUseCase(null)
        lastOffset = animalListResponse.lastOffset
        animalListResponse.animalList?.let { animalList ->
            if (animalList.size > 0
                    && listAdapter.animalItems.size > 0
                    && animalList[animalList.size - 1].id == listAdapter.animalItems[listAdapter.animalItems.size - 1].id) {
                shouldLoadMoreAnimals = false
            } else {
                listAdapter.animalItems = animalList.map { animalListItemFactory.newInstance(it) } as ArrayList<AnimalListItemViewModel>
            }
        }
    }

    private fun locationPermissionResponseListener(): PermissionListener {
        return object : PermissionListener {
            override fun onPermissionResult(response: PermissionRequestResponse) {
                if (response.permission == ACCESS_FINE_LOCATION) {
                    if (response.isGranted) {
                        dataStore += DiscoverErrorUseCase(null)
                        requestPermissionToLoad()
                    } else {
                        dataStore += DiscoverErrorUseCase(StatusCode.ERR_NO_LOCATION)
                    }
                }
            }
        }
    }
}
