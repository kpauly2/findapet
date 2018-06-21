package tech.pauly.findapet.shared

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem

import javax.inject.Inject

import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.ActivityMainBinding
import tech.pauly.findapet.discover.FilterActivity
import tech.pauly.findapet.shared.datastore.FilterAnimalTypeUseCase
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.ActivityEvent
import tech.pauly.findapet.shared.events.ViewEventBus

class MainActivity : BaseActivity() {

    @Inject
    internal lateinit var viewModel: MainViewModel

    @Inject
    internal lateinit var eventBus: ViewEventBus

    @Inject
    internal lateinit var dataStore: TransientDataStore

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        lifecycle.addObserver(viewModel)
        binding.viewModel = viewModel

        setupDrawer()
    }

    override fun onResume() {
        super.onResume()
        subscribeToDrawerChange()
        subscribeToExpandingLayoutChange()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return when (id) {
            R.id.menu_search -> {
                viewModel.currentAnimalType.get()?.let {
                    dataStore += FilterAnimalTypeUseCase(it)
                }
                activityEvent(ActivityEvent(this, FilterActivity::class.java, false))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun registerViewEvents(): CompositeDisposable? {
        val viewEvents = CompositeDisposable()

        viewEvents.add(eventBus.fragment(MainViewModel::class.java).subscribe(this::fragmentEvent))

        return viewEvents
    }

    private fun subscribeToDrawerChange() {
        viewModel.drawerSubject.subscribe({ binding.drawerLayout.closeDrawers() }, Throwable::printStackTrace).onLifecycle()
    }

    private fun subscribeToExpandingLayoutChange() {
        viewModel.expandingLayoutSubject.subscribe({ event ->
            when (event) {
                MainViewModel.ExpandingLayoutEvent.TOGGLE -> binding.expandingLayout.tapToggle()
                else -> binding.expandingLayout.collapse()
            }
        }, Throwable::printStackTrace).onLifecycle()
    }

    private fun setupDrawer() {
        val drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.also {
            it.setDisplayHomeAsUpEnabled(true)
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer, R.string.drawer_content).also {
                it.isDrawerIndicatorEnabled = true
                drawerLayout.addDrawerListener(it)
                it.syncState()
            }
        }
    }
}
