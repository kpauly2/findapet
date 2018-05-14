package tech.pauly.findapet.shared;

import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.favorites.FavoritesFragment;
import tech.pauly.findapet.shelters.SheltersFragment;

public class MainViewModel extends BaseViewModel {

    public ObservableInt defaultSelectedItem = new ObservableInt(R.id.navigation_discover);
    public BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = getOnNavigationItemSelectedListener();

    private ViewEventBus eventBus;

    @Inject
    public MainViewModel(ViewEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener getOnNavigationItemSelectedListener() {
        return item -> {
            FragmentEvent event = FragmentEvent.build(this).container(R.id.fragment_content);
            switch (item.getItemId()) {
                case R.id.navigation_discover:
                    eventBus.send(event.fragment(DiscoverFragment.class));
                    return true;
                case R.id.navigation_shelters:
                    eventBus.send(event.fragment(SheltersFragment.class));
                    return true;
                case R.id.navigation_favorites:
                    eventBus.send(event.fragment(FavoritesFragment.class));
                    return true;
            }
            return false;
        };
    }
}
