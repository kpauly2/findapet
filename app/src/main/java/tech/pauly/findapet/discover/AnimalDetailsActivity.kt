package tech.pauly.findapet.discover

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.Menu
import android.view.MenuItem
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
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
    internal lateinit var viewModel: AnimalDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityAnimalDetailsBinding>(this, R.layout.activity_animal_details)
        lifecycle.addObserver(viewModel)
        binding.viewModel = viewModel

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val typeface = ResourcesCompat.getFont(this, R.font.quicksand_bold)
        binding.collapsingToolbarLayout.setCollapsedTitleTypeface(typeface)
        binding.collapsingToolbarLayout.setExpandedTitleTypeface(typeface)
    }

    override fun registerViewEvents(): CompositeDisposable? {
        val viewEvents = CompositeDisposable()

        viewEvents += eventBus.optionsMenu(AnimalDetailsViewModel::class).subscribe(this::optionsMenuEvent)

        return viewEvents
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
}
