package tech.pauly.findapet.settings;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

public class SettingsViewModel extends BaseViewModel {

    private TransientDataStore dataStore;

    @Inject
    SettingsViewModel(TransientDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void updateToolbarTitle() {
        dataStore.save(new DiscoverToolbarTitleUseCase(R.string.menu_settings));
    }
}
