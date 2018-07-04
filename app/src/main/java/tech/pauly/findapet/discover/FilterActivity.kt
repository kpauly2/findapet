package tech.pauly.findapet.discover

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearSmoothScroller
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.ActivityFilterBinding
import tech.pauly.findapet.shared.BaseActivity
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class FilterActivity : BaseActivity() {

    @Inject
    lateinit var viewModel: FilterViewModel

    @Inject
    lateinit var eventBus: ViewEventBus

    private lateinit var binding: ActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter)
        lifecycle.addObserver(viewModel)
        binding.viewModel = viewModel

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.scrollToViewSubject.subscribe(this::scrollToBreedSearch, Throwable::printStackTrace).onLifecycle()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun scrollToBreedSearch(b: Boolean?) {
        val smoothScroller = object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 1
        binding.filterRecyclerView.layoutManager.startSmoothScroll(smoothScroller)
    }

    override fun registerViewEvents(): CompositeDisposable? {
        val viewEvents = CompositeDisposable()

        viewEvents += eventBus.activity(FilterViewModel::class).subscribe(this::activityEvent)

        return viewEvents
    }
}
