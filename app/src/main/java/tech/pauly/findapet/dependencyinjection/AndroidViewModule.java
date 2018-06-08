package tech.pauly.findapet.dependencyinjection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import tech.pauly.findapet.discover.AnimalDetailsActivity;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.discover.FilterActivity;
import tech.pauly.findapet.favorites.FavoritesFragment;
import tech.pauly.findapet.settings.SettingsFragment;
import tech.pauly.findapet.shared.MainActivity;
import tech.pauly.findapet.shelters.SheltersFragment;

@Module
public abstract class AndroidViewModule {

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector
    abstract DiscoverFragment bindDiscoverFragment();

    @ContributesAndroidInjector
    abstract SheltersFragment bindSheltersFragment();

    @ContributesAndroidInjector
    abstract FavoritesFragment bindFavoritesFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment bindSettingsFragment();

    @ContributesAndroidInjector
    abstract AnimalDetailsActivity bindAnimalDetailsActivity();

    @ContributesAndroidInjector
    abstract FilterActivity bindFilterActivity();
}
