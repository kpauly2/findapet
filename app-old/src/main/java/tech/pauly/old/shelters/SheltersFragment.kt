package tech.pauly.old.shelters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import tech.pauly.old.R
import tech.pauly.old.databinding.FragmentSheltersBinding
import tech.pauly.old.dependencyinjection.PetApplication
import tech.pauly.old.shared.BaseFragment
import tech.pauly.old.shared.MapWrapper
import javax.inject.Inject

class SheltersFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: SheltersViewModel

    @Inject
    lateinit var mapWrapper: MapWrapper

    override fun onAttach(context: Context?) {
        PetApplication.component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentSheltersBinding>(inflater, R.layout.fragment_shelters, container, false)
        addViewModelLifecycleObserver(viewModel)
        lifecycle.addObserver(mapWrapper)
        mapWrapper.setupMap(binding.mapView)
        binding.viewModel = viewModel
        return binding.root
    }
}
