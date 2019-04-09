package tech.pauly.findapet.discover

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.FragmentDiscoverBinding
import tech.pauly.findapet.dependencyinjection.PetApplication
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
        PetApplication.component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentDiscoverBinding>(inflater, R.layout.fragment_discover, container, false)
        binding.viewModel = viewModel
        binding.errorViewModel = errorViewModel
        addViewModelLifecycleObserver(viewModel)
        addViewModelLifecycleObserver(errorViewModel)
        return binding.root
    }

    override val viewEvents: CompositeDisposable?
        get() = CompositeDisposable().also {
            it += eventBus.activity(AnimalListItemViewModel::class).subscribe(this::activityEvent)
            it += eventBus.permission(DiscoverViewModel::class).subscribe(this::permissionEvent)
        }
}
