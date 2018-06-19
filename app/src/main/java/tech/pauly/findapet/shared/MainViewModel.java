package tech.pauly.findapet.shared;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;
import android.widget.ToggleButton;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import tech.pauly.findapet.R;
import tech.pauly.findapet.data.models.AnimalType;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.favorites.FavoritesFragment;
import tech.pauly.findapet.settings.SettingsFragment;
import tech.pauly.findapet.shared.datastore.DiscoverAnimalTypeUseCase;
import tech.pauly.findapet.shared.datastore.DiscoverToolbarTitleUseCase;
import tech.pauly.findapet.shared.datastore.TransientDataStore;
import tech.pauly.findapet.shared.events.FragmentEvent;
import tech.pauly.findapet.shared.events.ViewEventBus;
import tech.pauly.findapet.shelters.SheltersFragment;

public class MainViewModel extends BaseViewModel {

    private static final int NO_SELECTION = 0;

    public enum ExpandingLayoutEvent {
        TOGGLE, COLLAPSE

    }
    public ObservableInt toolbarTitle = new ObservableInt(R.string.empty_string);
    public ObservableField<AnimalType> currentAnimalType = new ObservableField<>();
    public ObservableInt currentButton = new ObservableInt(NO_SELECTION);

    private ViewEventBus eventBus;
    private TransientDataStore dataStore;
    private PublishSubject<ExpandingLayoutEvent> expandingLayoutSubject = PublishSubject.create();
    private PublishSubject<Boolean> drawerSubject = PublishSubject.create();
    private boolean firstLaunch;

    @Inject
    public MainViewModel(ViewEventBus eventBus, TransientDataStore dataStore) {
        this.eventBus = eventBus;
        this.dataStore = dataStore;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void clickFirstAnimal() {
        firstLaunch = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void subscribeToDataStore() {
        onLifecycle(dataStore.observeAndGetUseCase(DiscoverToolbarTitleUseCase.class)
                             .subscribe(useCase -> toolbarTitle.set(useCase.getTitle()),
                                                 Throwable::printStackTrace));
        if (firstLaunch) {
            clickAnimalType(AnimalType.CAT);
            firstLaunch = false;
        }
    }

    public void clickDiscover(View view) {
        expandingLayoutSubject.onNext(ExpandingLayoutEvent.TOGGLE);
        ToggleButton button = (ToggleButton) view;
        button.setChecked(button.isChecked());
    }

    public void clickShelters(View view) {
        clickStandardButton(view, SheltersFragment.class);
    }

    public void clickFavorites(View view) {
        clickStandardButton(view, FavoritesFragment.class);
    }

    public void clickSettings(View view) {
        clickStandardButton(view, SettingsFragment.class);
    }

    public void clickAnimalType(AnimalType type) {
        currentAnimalType.set(type);
        currentButton.set(NO_SELECTION);

        dataStore.save(new DiscoverAnimalTypeUseCase(type));
        launchFragment(DiscoverFragment.class);
    }

    public PublishSubject<ExpandingLayoutEvent> getExpandingLayoutSubject() {
        return expandingLayoutSubject;
    }

    public PublishSubject<Boolean> getDrawerSubject() {
        return drawerSubject;
    }

    private void clickStandardButton(View view, Class fragmentClass) {
        ToggleButton button = (ToggleButton) view;
        if (!button.isChecked()) {
            button.setChecked(true);
        } else {
            expandingLayoutSubject.onNext(ExpandingLayoutEvent.COLLAPSE);
            currentButton.set(view.getId());
        }
        launchFragment(fragmentClass);
        currentAnimalType.set(null);
    }

    private void launchFragment(Class clazz) {
        eventBus.send(new FragmentEvent(this, clazz, R.id.fragment_content));
        drawerSubject.onNext(true);
    }
}
