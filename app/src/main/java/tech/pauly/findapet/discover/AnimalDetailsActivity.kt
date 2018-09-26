package tech.pauly.findapet.discover

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.Menu
import android.view.MenuItem
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_animal_details.*
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.ActivityAnimalDetailsBinding
import tech.pauly.findapet.shared.BaseActivity
import tech.pauly.findapet.shared.events.OptionsMenuState
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class AnimalDetailsActivity : BaseActivity() {

    @Inject
    internal lateinit var eventBus: ViewEventBus

    @Inject
    internal lateinit var detailsPagerAdapter: AnimalDetailsViewPagerAdapter

    @Inject
    internal lateinit var imagesPagerAdapter: AnimalImagesPagerAdapter

    @Inject
    internal lateinit var viewModel: AnimalDetailsViewModel

    private lateinit var binding: ActivityAnimalDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_animal_details)
        addViewModelLifecycleObserver(viewModel)
        binding.viewModel = viewModel
        detailsPagerAdapter.viewModel = viewModel
        animal_images_view_pager.adapter = imagesPagerAdapter
        animal_details_view_pager.adapter = detailsPagerAdapter

        subscribeToImagesEvents()
        subscribeToTabEvents()
        setupToolbar()
    }

    override val viewEvents: CompositeDisposable?
        get() = CompositeDisposable().also {
            it += eventBus.optionsMenu(AnimalDetailsViewModel::class).subscribe(this::optionsMenuEvent)
            it += eventBus.snackbar(AnimalDetailsViewModel::class).subscribe(this::snackbarEvent)
            it += eventBus.activity(AnimalDetailsViewModel::class).subscribe(this::activityEvent)
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> viewModel.changeFavorite(true)
            R.id.menu_favorite_selected -> viewModel.changeFavorite(false)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        when (currentMenuState) {
            OptionsMenuState.FAVORITE -> menuInflater.inflate(R.menu.menu_favorite_selected, menu)
            OptionsMenuState.NOT_FAVORITE -> menuInflater.inflate(R.menu.menu_favorite, menu)
            OptionsMenuState.EMPTY -> menu.clear()
            else -> throw IllegalStateException("OptionsMenuState $currentMenuState not supported in AnimalDetailsActivity")
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val typeface = ResourcesCompat.getFont(this, R.font.quicksand_bold)
        collapsing_toolbar_layout.setCollapsedTitleTypeface(typeface)
        collapsing_toolbar_layout.setExpandedTitleTypeface(typeface)
    }

    private fun subscribeToImagesEvents() {
        viewModel.animalImagesSubject.quickSubscribe {
            imagesPagerAdapter.setAnimalImages(it)
        }
    }

    private fun subscribeToTabEvents() {
        viewModel.tabSwitchSubject.quickSubscribe {
            binding.animalDetailsViewPager.currentItem = it
        }
    }
}
