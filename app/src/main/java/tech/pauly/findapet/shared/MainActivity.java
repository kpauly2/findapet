package tech.pauly.findapet.shared;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.disposables.CompositeDisposable;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    @Inject
    MainViewModel viewModel;

    @Inject
    ViewEventBus eventBus;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ActivityMainBinding binding = this.binding;
        getLifecycle().addObserver(viewModel);
        binding.setViewModel(viewModel);
        viewModel.setDrawer(binding.drawerLayout);

        setupDrawer();
    }

    @Nullable
    @Override
    protected CompositeDisposable registerViewEvents() {
        CompositeDisposable viewEvents = new CompositeDisposable();

        viewEvents.add(eventBus.fragment(MainViewModel.class).subscribe(this::fragmentEvent));

        return viewEvents;
    }

    private void setupDrawer() {
        DrawerLayout drawerLayout = binding.drawerLayout;
        ActionBarDrawerToggle drawerToggle;
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer, R.string.drawer_content);
            drawerToggle.setDrawerIndicatorEnabled(true);

            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }
    }
}
