package tech.pauly.findapet.discover;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.disposables.CompositeDisposable;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.ActivityFilterBinding;
import tech.pauly.findapet.shared.BaseActivity;
import tech.pauly.findapet.shared.events.ViewEventBus;

public class FilterActivity extends BaseActivity {

    @Inject
    FilterViewModel viewModel;

    @Inject
    ViewEventBus eventBus;

    private ActivityFilterBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        getLifecycle().addObserver(viewModel);
        binding.setViewModel(viewModel);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        subscribeOnLifecycle(viewModel.getScrollToViewSubject()
                                      .subscribe(this::scrollToBreedSearch, Throwable::printStackTrace));
    }

    private void scrollToBreedSearch(Boolean b) {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(1);
        binding.filterRecyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    @Nullable
    @Override
    protected CompositeDisposable registerViewEvents() {
        CompositeDisposable viewEvents = new CompositeDisposable();

        viewEvents.add(eventBus.activity(FilterViewModel.class).subscribe(this::activityEvent));

        return viewEvents;
    }
}
