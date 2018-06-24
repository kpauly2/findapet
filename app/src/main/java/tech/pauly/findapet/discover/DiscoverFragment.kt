package tech.pauly.findapet.discover

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.FragmentDiscoverBinding
import tech.pauly.findapet.shared.BaseFragment
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class DiscoverFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: DiscoverViewModel

    @Inject
    lateinit var errorViewModel: DiscoverErrorViewModel

    @Inject
    lateinit var eventBus: ViewEventBus

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentDiscoverBinding>(inflater, R.layout.fragment_discover, container, false)
        binding.viewModel = viewModel
        binding.errorViewModel = errorViewModel
        lifecycle.addObserver(viewModel)
        lifecycle.addObserver(errorViewModel)
        return binding.root
    }

    override fun registerViewEvents(): CompositeDisposable? {
        val viewEvents = CompositeDisposable()

        viewEvents += eventBus.activity(AnimalListItemViewModel::class).subscribe(this::activityEvent)
        viewEvents += eventBus.permission(DiscoverViewModel::class).subscribe(this::permissionEvent)

        return viewEvents
    }
}
