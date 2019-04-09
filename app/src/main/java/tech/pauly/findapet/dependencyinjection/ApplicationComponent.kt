package tech.pauly.findapet.dependencyinjection

import javax.inject.Singleton

import dagger.Component
import tech.pauly.findapet.discover.AnimalDetailsActivity
import tech.pauly.findapet.discover.DiscoverFragment
import tech.pauly.findapet.discover.FilterActivity
import tech.pauly.findapet.favorites.FavoritesFragment
import tech.pauly.findapet.settings.SettingsFragment
import tech.pauly.findapet.shared.MainActivity
import tech.pauly.findapet.shared.PermissionHelper
import tech.pauly.findapet.shelters.SheltersFragment

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
