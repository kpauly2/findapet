package tech.pauly.findapet.shared

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import android.widget.ToggleButton

import javax.inject.Inject

import io.reactivex.subjects.PublishSubject
import tech.pauly.findapet.R
import tech.pauly.findapet.data.models.AnimalType
import tech.pauly.findapet.discover.DiscoverFragment
import tech.pauly.findapet.favorites.FavoritesFragment
import tech.pauly.findapet.settings.SettingsFragment
import tech.pauly.findapet.shared.datastore.DiscoverAnimalTypeUseCase
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.FragmentEvent
import tech.pauly.findapet.shared.events.ViewEventBus
import tech.pauly.findapet.shelters.SheltersFragment
import kotlin.reflect.KClass

private const val NO_SELECTION = 0

class MainViewModel @Inject
constructor(private val eventBus: ViewEventBus,
            private val dataStore: TransientDataStore) : BaseViewModel() {

    enum class ExpandingLayoutEvent {
        TOGGLE, COLLAPSE
    }

    var toolbarTitle = ObservableInt(R.string.empty_string)
    var currentAnimalType = ObservableField<AnimalType>()
    var currentButton = ObservableInt(NO_SELECTION)

    val expandingLayoutSubject = PublishSubject.create<ExpandingLayoutEvent>()
    val drawerSubject = PublishSubject.create<Boolean>()

    private var firstLaunch = false

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun clickFirstAnimal() {
        firstLaunch = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun subscribeToDataStore() {
        dataStore.observeAndGetUseCase(DiscoverToolbarTitleUseCase::class)
                .subscribe({ useCase -> toolbarTitle.set(useCase.title) }, Throwable::printStackTrace)
                .onLifecycle()
        if (firstLaunch) {
            clickAnimalType(AnimalType.CAT)
            firstLaunch = false
        }
    }

    fun clickDiscover(view: View) {
        expandingLayoutSubject.onNext(ExpandingLayoutEvent.TOGGLE)
        val button = view as ToggleButton
        button.isChecked = button.isChecked
    }

    fun clickShelters(view: View) {
        clickStandardButton(view, SheltersFragment::class)
    }

    fun clickFavorites(view: View) {
        clickStandardButton(view, FavoritesFragment::class)
    }

    fun clickSettings(view: View) {
        clickStandardButton(view, SettingsFragment::class)
    }

    fun clickAnimalType(type: AnimalType) {
        currentAnimalType.set(type)
        currentButton.set(NO_SELECTION)

        dataStore += DiscoverAnimalTypeUseCase(type)
        launchFragment(DiscoverFragment::class)
    }

    private fun clickStandardButton(view: View, fragmentClass: KClass<*>) {
        val button = view as ToggleButton
        if (!button.isChecked) {
            button.isChecked = true
        } else {
            expandingLayoutSubject.onNext(ExpandingLayoutEvent.COLLAPSE)
            currentButton.set(view.getId())
        }
        launchFragment(fragmentClass)
        currentAnimalType.set(null)
    }

    private fun launchFragment(clazz: KClass<*>) {
        eventBus += FragmentEvent(this, clazz, R.id.fragment_content)
        drawerSubject.onNext(true)
    }
}
