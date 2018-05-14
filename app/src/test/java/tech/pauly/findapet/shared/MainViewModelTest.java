package tech.pauly.findapet.shared;

import android.view.MenuItem;

import org.junit.Before;
import org.junit.Test;
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

public class MainViewModelTest {

    @Mock
    private ViewEventBus eventBus;

    private MainViewModel subject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        subject = new MainViewModel(eventBus);
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

        assertThat(subject.onNavigationItemSelectedListener.onNavigationItemSelected(menuItem)).isTrue();
        verify(eventBus).send(FragmentEvent.build(subject).container(R.id.fragment_content).fragment(DiscoverFragment.class));
    }

    @Test
    public void onNavigationItemSelected_menuItemShelters_returnTrueAndLaunchSheltersFragment() {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(R.id.navigation_shelters);

        assertThat(subject.onNavigationItemSelectedListener.onNavigationItemSelected(menuItem)).isTrue();
        verify(eventBus).send(FragmentEvent.build(subject).container(R.id.fragment_content).fragment(SheltersFragment.class));
    }

    @Test
    public void onNavigationItemSelected_menuItemFavorites_returnTrueAndLaunchFavoritesFragment() {
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(R.id.navigation_favorites);

        assertThat(subject.onNavigationItemSelectedListener.onNavigationItemSelected(menuItem)).isTrue();
        verify(eventBus).send(FragmentEvent.build(subject).container(R.id.fragment_content).fragment(FavoritesFragment.class));
    }
}