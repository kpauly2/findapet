package tech.pauly.findapet.shared

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import dagger.android.AndroidInjection
import io.fabric.sdk.android.Fabric
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_main.*
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.ActivityMainBinding
import tech.pauly.findapet.discover.AnimalListItemViewModel
import tech.pauly.findapet.discover.DiscoverViewModel
import tech.pauly.findapet.discover.FilterActivity
import tech.pauly.findapet.favorites.FavoritesViewModel
import tech.pauly.findapet.settings.SettingsViewModel
import tech.pauly.findapet.shared.datastore.FilterAnimalTypeUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus
import tech.pauly.findapet.shelters.SheltersViewModel
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    internal lateinit var viewModel: MainViewModel

    @Inject
    internal lateinit var eventBus: ViewEventBus

    @Inject
    internal lateinit var dataStore: TransientDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        Fabric.with(this, Crashlytics())

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        lifecycle.addObserver(viewModel)
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
                .subscribe({ drawer_layout.closeDrawers() }, Throwable::printStackTrace)
                .onLifecycle()
    }

    private fun subscribeToExpandingLayoutChange() {
        viewModel.expandingLayoutSubject.subscribe({ event ->
            when (event) {
                MainViewModel.ExpandingLayoutEvent.TOGGLE -> expanding_layout.tapToggle()
                else -> expanding_layout.collapse()
            }
        }, Throwable::printStackTrace).onLifecycle()
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
