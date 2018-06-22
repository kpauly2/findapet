package tech.pauly.findapet.dependencyinjection

import dagger.Module
import dagger.android.ContributesAndroidInjector
import tech.pauly.findapet.discover.AnimalDetailsActivity
import tech.pauly.findapet.discover.DiscoverFragment
import tech.pauly.findapet.discover.FilterActivity
import tech.pauly.findapet.favorites.FavoritesFragment
import tech.pauly.findapet.settings.SettingsFragment
import tech.pauly.findapet.shared.MainActivity
import tech.pauly.findapet.shelters.SheltersFragment

@Module
abstract class AndroidViewModule {

    @ContributesAndroidInjector
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindDiscoverFragment(): DiscoverFragment

    @ContributesAndroidInjector
    internal abstract fun bindSheltersFragment(): SheltersFragment

    @ContributesAndroidInjector
    internal abstract fun bindFavoritesFragment(): FavoritesFragment

    @ContributesAndroidInjector
    internal abstract fun bindSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    internal abstract fun bindAnimalDetailsActivity(): AnimalDetailsActivity

    @ContributesAndroidInjector
    internal abstract fun bindFilterActivity(): FilterActivity
}
