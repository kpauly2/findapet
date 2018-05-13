package tech.pauly.findapet.shared;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.disposables.CompositeDisposable;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.ActivityMainTabBinding;

public class MainTabActivity extends BaseActivity {

    @Inject
    MainTabViewModel viewModel;

    @Inject
    ViewEventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        ActivityMainTabBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_tab);
        getLifecycle().addObserver(viewModel);
        binding.setViewModel(viewModel);
    }

    @Nullable
    @Override
    protected CompositeDisposable registerViewEvents() {
        CompositeDisposable viewEvents = new CompositeDisposable();

        viewEvents.add(eventBus.fragment(MainTabViewModel.class).subscribe(this::fragmentEvent));

        return viewEvents;
    }
}
