package tech.pauly.findapet.discover

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_filter.*
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.ActivityFilterBinding
import tech.pauly.findapet.dependencyinjection.PetApplication
import tech.pauly.findapet.shared.BaseActivity
import tech.pauly.findapet.shared.events.ViewEventBus
import tech.pauly.findapet.utils.addAnimationListener
import tech.pauly.findapet.utils.runAnimation
import javax.inject.Inject

class FilterActivity : BaseActivity() {

    @Inject
    lateinit var viewModel: FilterViewModel

    @Inject
    lateinit var eventBus: ViewEventBus

    private lateinit var breedViewModel: BreedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        PetApplication.component.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityFilterBinding>(this, R.layout.activity_filter)
        addViewModelLifecycleObserver(viewModel)
        binding.viewModel = viewModel
        breedViewModel = viewModel.breedViewModel.also {
            addViewModelLifecycleObserver(it)
            binding.breedViewModel = it
        }

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    override fun onResume() {
        super.onResume()
        handleSearch()
    }

    private fun handleSearch() {
        breedViewModel.openBreedSubject.quickSubscribe { open ->
            if (open) showBreed() else hideBreed()
        }
    }

    override val viewEvents: CompositeDisposable?
        get() = CompositeDisposable().also {
            it += eventBus.activity(FilterViewModel::class).subscribe(this::activityEvent)
        }

    private fun showBreed() {
        breed_layout.visibility = View.VISIBLE
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        breed_layout.runAnimation { _ ->
            AnimationUtils.loadAnimation(this, R.anim.scroll_bottom_to_center)
                    .addAnimationListener(
                            animationEnd = {
                                breed_search_edit_text.requestFocus()
                                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                            })
        }
    }

    private fun hideBreed() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        breed_layout.runAnimation { layout ->
            AnimationUtils.loadAnimation(this, R.anim.scroll_center_to_bottom)
                    .addAnimationListener(
                            animationStart = {
                                inputManager.hideSoftInputFromWindow(breed_search_edit_text.windowToken, 0)
                                breed_search_edit_text.clearFocus()
                            },
                            animationEnd = {
                                layout.visibility = View.GONE
                            })
        }
    }
}
