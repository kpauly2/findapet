package tech.pauly.findapet.discover;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.CompositeDisposable;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.FragmentDiscoverBinding;
import tech.pauly.findapet.shared.BaseFragment;
import tech.pauly.findapet.shared.ViewEventBus;

public class DiscoverFragment extends BaseFragment {

    @Inject
    DiscoverViewModel discoverViewModel;

    @Inject
    ViewEventBus eventBus;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentDiscoverBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover, container, false);
        binding.setViewModel(discoverViewModel);
        getLifecycle().addObserver(discoverViewModel);
        return binding.getRoot();
    }

    @Nullable
    @Override
    protected CompositeDisposable registerViewEvents() {
        CompositeDisposable viewEvents = new CompositeDisposable();

        viewEvents.add(eventBus.activity(AnimalListItemViewModel.class).subscribe(this::activityEvent));

        return viewEvents;
    }
}
