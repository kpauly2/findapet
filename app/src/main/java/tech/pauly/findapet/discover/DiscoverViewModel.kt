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
import tech.pauly.findapet.shared.events.PermissionEvent
import tech.pauly.findapet.shared.events.PermissionListener
import tech.pauly.findapet.shared.events.ViewEventBus
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
    var locationMissing = ObservableBoolean(false)
    var animalsMissing = ObservableBoolean(false)
    var chipList: ObservableList<Chip> = ObservableArrayList()
    var locationChip = ObservableField<Chip>()

    private var animalType = AnimalType.CAT
    private var lastOffset = 0
    private var firstLoad = true

    val errorVisible: Boolean
        get() = locationMissing.get() || animalsMissing.get()

    private val currentFilter: Observable<Filter>
        get() = filterRepository.currentFilterAndNoFilterIfEmpty
                .doOnSubscribe(this::removeFilterChips)
                .doOnSuccess(this::addFilterChips).toObservable()

    private val currentLocation: Observable<Address>
        get() = locationHelper.fetchCurrentLocation()
                .doOnSubscribe(this::removeLocationChip)
                .doOnNext(this::addLocationChip)

    init {
        dataStore[DiscoverAnimalTypeUseCase::class.java]?.let {
            animalType = it.animalType
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        val useCase = dataStore[FilterUpdatedUseCase::class.java]
        if (firstLoad || useCase != null) {
            firstLoad = false
            requestPermissionToLoad()
        }
    }

    fun requestPermissionToLoad() {
        if (permissionHelper.hasPermissions(ACCESS_FINE_LOCATION)) {
            loadList()
        } else {
            eventBus.send(PermissionEvent(this,
                    arrayOf(ACCESS_FINE_LOCATION),
                    locationPermissionResponseListener(),
                    100))
        }
    }

    private fun loadList() {
        dataStore += DiscoverToolbarTitleUseCase(animalType.toolbarName)
        listAdapter.clearAnimalItems()
        lastOffset = 0
        fetchAnimals()
    }

    fun loadMoreAnimals() {
        fetchAnimals()
    }

    private fun fetchAnimals() {
        refreshing.set(true)
        Observable.zip<Address, Filter, FetchAnimalsRequest>(currentLocation,
                currentFilter, BiFunction { location, filter -> FetchAnimalsRequest(animalType, lastOffset, location.postalCode, filter) })
                .flatMap(animalRepository::fetchAnimals)
                .subscribe(this::setAnimalList, this::showError)
                .onLifecycle()
    }

    private fun removeFilterChips(disposable: Disposable) {
        chipList.clear()
    }

    private fun addFilterChips(filter: Filter) {
        if (filter.sex !== Sex.MISSING) {
            chipList.add(Chip(resourceProvider.getString(filter.sex.formattedName)))
        }

        if (filter.age !== Age.MISSING) {
            chipList.add(Chip(resourceProvider.getString(filter.age.formattedName)))
        }

        if (filter.size !== AnimalSize.MISSING) {
            chipList.add(Chip(resourceProvider.getString(filter.size.formattedName)))
        }

        if (filter.breed != "") {
            chipList.add(Chip(filter.breed))
        }
    }

    private fun removeLocationChip(disposable: Disposable) {
        locationChip.set(null)
    }

    private fun addLocationChip(location: Address) {
        locationChip.set(Chip(resourceProvider.getString(R.string.chip_near_location, location.postalCode)))
    }

    private fun showError(throwable: Throwable) {
        refreshing.set(false)
        if (throwable is PetfinderException) {
            return when (throwable.statusCode) {
                StatusCode.ERR_NO_ANIMALS -> {
                    animalsMissing.set(true)
                }
                else -> {
                }
            }
        }
        throwable.printStackTrace()
    }

    private fun setAnimalList(animalListResponse: AnimalListResponse) {
        refreshing.set(false)
        animalsMissing.set(false)
        lastOffset = animalListResponse.lastOffset
        animalListResponse.animalList?.let { animalList ->
            listAdapter.animalItems = animalList.map { animalListItemFactory.newInstance(it) } as ArrayList<AnimalListItemViewModel>
        }
    }

    private fun locationPermissionResponseListener(): PermissionListener {
        return object : PermissionListener {
            override fun onPermissionResult(response: PermissionRequestResponse) {
                if (response.permission == ACCESS_FINE_LOCATION) {
                    if (response.isGranted) {
                        locationMissing.set(false)
                        requestPermissionToLoad()
                    } else {
                        locationMissing.set(true)
                    }
                }
            }
        }
    }
}
