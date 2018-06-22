package tech.pauly.findapet.favorites

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.FragmentFavoritesBinding
import tech.pauly.findapet.shared.BaseFragment
import javax.inject.Inject

class FavoritesFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: FavoritesViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentFavoritesBinding>(inflater, R.layout.fragment_favorites, container, false)
        lifecycle.addObserver(viewModel)
        return binding.root
    }
}
