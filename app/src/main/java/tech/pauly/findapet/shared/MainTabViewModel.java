package tech.pauly.findapet.shared;

import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;

import javax.inject.Inject;

import tech.pauly.findapet.R;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.favorites.FavoritesFragment;
import tech.pauly.findapet.shelters.SheltersFragment;

public class MainTabViewModel extends BaseViewModel {

    public ObservableInt defaultSelectedItem = new ObservableInt(R.id.navigation_discover);
    public BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = getOnNavigationItemSelectedListener();
    // TODO: make not a direct reference: https://www.pivotaltracker.com/story/show/157172208
    private MainTabActivity activity;

    @Inject
    public MainTabViewModel() {}

    public void setActivity(MainTabActivity activity) {
        this.activity = activity;
    }

    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener getOnNavigationItemSelectedListener() {
        return item -> {
            switch (item.getItemId()) {
                case R.id.navigation_discover:
                    // TODO: don't create these view objects in the VM: https://www.pivotaltracker.com/story/show/157172208
                    activity.launchTabFragment(new DiscoverFragment());
                    return true;
                case R.id.navigation_shelters:
                    activity.launchTabFragment(new SheltersFragment());
                    return true;
                case R.id.navigation_favorites:
                    activity.launchTabFragment(new FavoritesFragment());
                    return true;
            }
            return false;
        };
    }
}
