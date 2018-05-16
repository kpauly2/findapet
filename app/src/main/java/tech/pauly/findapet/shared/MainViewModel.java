package tech.pauly.findapet.shared;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableInt;
import android.support.v4.widget.DrawerLayout;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.favorites.FavoritesFragment;
import tech.pauly.findapet.settings.SettingsFragment;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shelters.SheltersFragment;

public class MainViewModel extends BaseViewModel {
    private static final int CLEAR_CHECKED = -1;

    public ObservableInt toolbarTitle = new ObservableInt(R.string.empty_string);

    private ViewEventBus eventBus;
    private TransientDataStore dataStore;
    private DrawerLayout drawerLayout;

    @Inject
    public MainViewModel(ViewEventBus eventBus, TransientDataStore dataStore) {
        this.eventBus = eventBus;
        this.dataStore = dataStore;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void subscribeToDataStore() {
        subscribeOnLifecycle(dataStore.observeAndGetUseCase(DiscoverToolbarTitleUseCase.class)
                                      .subscribe(useCase -> toolbarTitle.set(useCase.getTitle()),
                                                 Throwable::printStackTrace));
    }

    public void checkChanged(int checkedId) {
        Class fragmentToLaunch;
        switch (checkedId) {
            case R.id.discover_button:
                fragmentToLaunch = DiscoverFragment.class;
                break;
            case R.id.shelters_button:
                fragmentToLaunch = SheltersFragment.class;
                break;
            case R.id.favorites_button:
                fragmentToLaunch = FavoritesFragment.class;
                break;
            case R.id.settings_button:
                fragmentToLaunch = SettingsFragment.class;
                break;
            case CLEAR_CHECKED:
                return;
            default:
                throw new IllegalStateException("Checked ID " + checkedId + " not handled");
        }
        eventBus.send(FragmentEvent.build(this)
                                   .container(R.id.fragment_content)
                                   .fragment(fragmentToLaunch));
        drawerLayout.closeDrawers();
    }

    public void setDrawer(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }
}
