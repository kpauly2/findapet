package tech.pauly.old.shared

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*
import tech.pauly.old.R
import tech.pauly.old.databinding.ActivityMainBinding
import tech.pauly.old.dependencyinjection.PetApplication
import tech.pauly.old.discover.AnimalListItemViewModel
import tech.pauly.old.discover.DiscoverViewModel
import tech.pauly.old.discover.FilterActivity
import tech.pauly.old.favorites.FavoritesViewModel
import tech.pauly.old.settings.SettingsViewModel
import tech.pauly.old.shared.datastore.FilterAnimalTypeUseCase
import tech.pauly.old.shared.datastore.TransientDataStore
import tech.pauly.old.shared.events.ActivityEvent
import tech.pauly.old.shared.events.OptionsMenuState
import tech.pauly.old.shared.events.ViewEventBus
import tech.pauly.old.shelters.SheltersViewModel
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    internal lateinit var viewModel: MainViewModel

    @Inject
    internal lateinit var eventBus: ViewEventBus

    @Inject
    internal lateinit var dataStore: TransientDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        PetApplication.component.inject(this)
        super.onCreate(savedInstanceState)

        Fabric.with(this, Crashlytics())

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        addViewModelLifecycleObserver(viewModel)
        binding.viewModel = viewModel

        setupDrawer()
    }

    override fun onResume() {
        super.onResume()
        subscribeToDrawerChange()
        subscribeToExpandingLayoutChange()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return when (id) {
            R.id.menu_search -> {
                viewModel.currentAnimalType.get()?.let {
                    dataStore += FilterAnimalTypeUseCase(it)
                }
                activityEvent(ActivityEvent(this, FilterActivity::class, false))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override val viewEvents: CompositeDisposable?
        get() = CompositeDisposable().also {
            it += eventBus.fragment(MainViewModel::class).subscribe(this::fragmentEvent)
            it += eventBus.optionsMenu(DiscoverViewModel::class).subscribe(this::optionsMenuEvent)
            it += eventBus.optionsMenu(SheltersViewModel::class).subscribe(this::optionsMenuEvent)
            it += eventBus.optionsMenu(FavoritesViewModel::class).subscribe(this::optionsMenuEvent)
            it += eventBus.optionsMenu(SettingsViewModel::class).subscribe(this::optionsMenuEvent)
            it += eventBus.dialog(AnimalListItemViewModel::class).subscribe(this::dialogEvent)
            it += eventBus.dialog(FavoritesViewModel::class).subscribe(this::dialogEvent)
        }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        when (currentMenuState) {
            OptionsMenuState.DISCOVER -> menuInflater.inflate(R.menu.menu_search, menu)
            OptionsMenuState.EMPTY -> menu.clear()
            else -> throw IllegalStateException("OptionsMenuState $currentMenuState not supported in MainActivity")
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun subscribeToDrawerChange() {
        viewModel.drawerSubject
                .quickSubscribe { drawer_layout.closeDrawers() }
    }

    private fun subscribeToExpandingLayoutChange() {
        viewModel.expandingLayoutSubject.quickSubscribe { event ->
            when (event) {
                MainViewModel.ExpandingLayoutEvent.TOGGLE -> expanding_layout.tapToggle()
                else -> expanding_layout.collapse()
            }
        }
    }

    private fun setupDrawer() {
        setSupportActionBar(toolbar)
        supportActionBar?.also {
            it.setDisplayHomeAsUpEnabled(true)
            ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer, R.string.drawer_content).also {
                it.isDrawerIndicatorEnabled = true
                drawer_layout.addDrawerListener(it)
                it.syncState()
            }
        }
    }
}
