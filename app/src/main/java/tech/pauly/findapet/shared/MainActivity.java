package tech.pauly.findapet.shared;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.disposables.CompositeDisposable;
import tech.pauly.findapet.R;
import tech.pauly.findapet.databinding.ActivityMainBinding;
import tech.pauly.findapet.discover.FilterActivity;
import tech.pauly.findapet.shared.datastore.FilterAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;

public class MainActivity extends BaseActivity {

    @Inject
    MainViewModel viewModel;

    @Inject
    ViewEventBus eventBus;

    @Inject
    TransientDataStore dataStore;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ActivityMainBinding binding = this.binding;
        getLifecycle().addObserver(viewModel);
        binding.setViewModel(viewModel);

        setupDrawer();
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribeToDrawerChange();
        subscribeToExpandingLayoutChange();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_search:
                dataStore.save(new FilterAnimalTypeUseCase(viewModel.currentAnimalType.get()));
                activityEvent(ActivityEvent.build(this).startActivity(FilterActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    protected CompositeDisposable registerViewEvents() {
        CompositeDisposable viewEvents = new CompositeDisposable();

        viewEvents.add(eventBus.fragment(MainViewModel.class).subscribe(this::fragmentEvent));

        return viewEvents;
    }

    private void subscribeToDrawerChange() {
        subscribeOnLifecycle(viewModel.getDrawerSubject()
                                      .subscribe(bool -> binding.drawerLayout.closeDrawers(),
                                                 Throwable::printStackTrace));
    }

    private void subscribeToExpandingLayoutChange() {
        subscribeOnLifecycle(viewModel.getExpandingLayoutSubject().subscribe(event -> {
            if (event == MainViewModel.ExpandingLayoutEvent.TOGGLE) {
                binding.expandingLayout.tapToggle();
            } else {
                binding.expandingLayout.collapse();
            }
        }, Throwable::printStackTrace));
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
