package tech.pauly.old.dependencyinjection

import javax.inject.Singleton

import dagger.Component
import tech.pauly.old.discover.AnimalDetailsActivity
import tech.pauly.old.discover.DiscoverFragment
import tech.pauly.old.discover.FilterActivity
import tech.pauly.old.favorites.FavoritesFragment
import tech.pauly.old.settings.SettingsFragment
import tech.pauly.old.shared.MainActivity
import tech.pauly.old.shared.PermissionHelper
import tech.pauly.old.shelters.SheltersFragment

@Singleton
@Component(modules = [
    ApplicationModule::class,
    DataModule::class])
interface ApplicationComponent {
    fun permissionHelper(): PermissionHelper

    fun inject(mainActivity: MainActivity)
    fun inject(discoverFragment: DiscoverFragment)
    fun inject(sheltersFragment: SheltersFragment)
    fun inject(favoritesFragment: FavoritesFragment)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(animalDetailsActivity: AnimalDetailsActivity)
    fun inject(filterActivity: FilterActivity)
}
