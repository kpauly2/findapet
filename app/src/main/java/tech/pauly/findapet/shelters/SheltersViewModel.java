package tech.pauly.findapet.shelters;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.shared.BaseViewModel;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

public class SheltersViewModel extends BaseViewModel {

    private TransientDataStore dataStore;

    @Inject
    SheltersViewModel(TransientDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void updateToolbarTitle() {
        dataStore.save(new DiscoverToolbarTitleUseCase(R.string.menu_shelters));
    }
}
