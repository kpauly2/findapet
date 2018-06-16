package tech.pauly.findapet.shelters

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.FragmentSheltersBinding
import tech.pauly.findapet.shared.BaseFragment
import tech.pauly.findapet.shared.MapWrapper
import javax.inject.Inject

class SheltersFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: SheltersViewModel

    @Inject
    lateinit var mapWrapper: MapWrapper

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentSheltersBinding>(inflater, R.layout.fragment_shelters, container, false)
        lifecycle.addObserver(viewModel)
        lifecycle.addObserver(mapWrapper)
        mapWrapper.setupMap(binding.mapView)
        return binding.root
    }
}
