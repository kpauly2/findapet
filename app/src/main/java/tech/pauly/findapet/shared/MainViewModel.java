package tech.pauly.findapet.shared;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableInt;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;

public class MainViewModel extends BaseViewModel {

    public ObservableInt toolbarTitle = new ObservableInt(R.string.empty_string);

    private ViewEventBus eventBus;
    private TransientDataStore dataStore;

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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void temp() {
        eventBus.send(FragmentEvent.build(this)
                                   .container(R.id.fragment_content)
                                   .fragment(DiscoverFragment.class));
    }
}
