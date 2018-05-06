package tech.pauly.findapet.shared;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tech.pauly.findapet.R;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.favorites.FavoritesFragment;
import tech.pauly.findapet.shelters.SheltersFragment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainTabViewModelTest {

    @Mock
    private MainTabActivity activity;

    private MainTabViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        subject = new MainTabViewModel();
        subject.setActivity(activity);
    }

    @Test
    public void onNavigationItemSelected_menuItemNotSupported_returnFalse() {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(0);

        assertThat(subject.onNavigationItemSelectedListener.onNavigationItemSelected(menuItem)).isFalse();
    }

    @Test
    public void onNavigationItemSelected_menuItemDiscover_returnTrueAndLaunchDiscoverFragment() {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(R.id.navigation_discover);
        ArgumentCaptor<Fragment> captor = ArgumentCaptor.forClass(Fragment.class);

        assertThat(subject.onNavigationItemSelectedListener.onNavigationItemSelected(menuItem)).isTrue();
        verify(activity).launchTabFragment(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(DiscoverFragment.class);
    }

    @Test
    public void onNavigationItemSelected_menuItemShelters_returnTrueAndLaunchSheltersFragment() {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(R.id.navigation_shelters);
        ArgumentCaptor<Fragment> captor = ArgumentCaptor.forClass(Fragment.class);

        assertThat(subject.onNavigationItemSelectedListener.onNavigationItemSelected(menuItem)).isTrue();
        verify(activity).launchTabFragment(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(SheltersFragment.class);
    }

    @Test
    public void onNavigationItemSelected_menuItemFavorites_returnTrueAndLaunchFavoritesFragment() {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(R.id.navigation_favorites);
        ArgumentCaptor<Fragment> captor = ArgumentCaptor.forClass(Fragment.class);

        assertThat(subject.onNavigationItemSelectedListener.onNavigationItemSelected(menuItem)).isTrue();
        verify(activity).launchTabFragment(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(FavoritesFragment.class);
    }
}