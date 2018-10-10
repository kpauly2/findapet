package tech.pauly.findapet.settings

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import javax.inject.Inject

import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import tech.pauly.findapet.R
import tech.pauly.findapet.databinding.FragmentSettingsBinding
import tech.pauly.findapet.shared.BaseFragment
import tech.pauly.findapet.shared.events.ViewEventBus

class SettingsFragment : BaseFragment() {

    @Inject
    lateinit var eventBus: ViewEventBus

    @Inject
    lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(inflater, R.layout.fragment_settings, container, false)
        binding.viewModel = viewModel
        addViewModelLifecycleObserver(viewModel)
        return binding.root
    }

    override val viewEvents: CompositeDisposable?
        get() = CompositeDisposable().also {
            it += eventBus.activity(SettingsLinkOutViewModel::class).subscribe(this::activityEvent)
            it += eventBus.activity(SettingsEmailViewModel::class).subscribe(this::activityEvent)
        }
}
