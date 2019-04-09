package tech.pauly.findapet.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.FragmentFavoritesBinding
import tech.pauly.findapet.dependencyinjection.PetApplication
import tech.pauly.findapet.discover.AnimalListItemViewModel
import tech.pauly.findapet.shared.BaseFragment
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Inject

class FavoritesFragment : BaseFragment() {

    @Inject
    lateinit var eventBus: ViewEventBus

    @Inject
    lateinit var viewModel: FavoritesViewModel

    override fun onAttach(context: Context?) {
        PetApplication.component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentFavoritesBinding>(inflater, R.layout.fragment_favorites, container, false)
        binding.viewModel = viewModel
        addViewModelLifecycleObserver(viewModel)
        return binding.root
    }

    override val viewEvents: CompositeDisposable?
        get() = CompositeDisposable().also {
            it += eventBus.activity(AnimalListItemViewModel::class).subscribe(this::activityEvent)
        }
}
