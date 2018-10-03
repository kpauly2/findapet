package tech.pauly.findapet.dependencyinjection

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import tech.pauly.findapet.data.AnimalRepository
import tech.pauly.findapet.data.FavoriteRepository
import tech.pauly.findapet.discover.AnimalListAdapter
import tech.pauly.findapet.discover.AnimalListItemViewModel
import tech.pauly.findapet.favorites.FavoritesViewModel
import tech.pauly.findapet.shared.ResourceProvider
import tech.pauly.findapet.shared.datastore.TransientDataStore
import tech.pauly.findapet.shared.events.ViewEventBus
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
class ViewModelModule {
    @Provides
    @Singleton
    fun provideViewModelFactory(providers: Map<Class<out ViewModel>, Provider<ViewModel>>) = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return requireNotNull(providers[modelClass as Class<out ViewModel>]).get() as T
        }
    }

    @Provides
    @IntoMap
    @ViewModelKey(FavoritesViewModel::class)
    fun provideFavoritesViewModel(listAdapter: AnimalListAdapter,
                                  animalListItemFactory: AnimalListItemViewModel.Factory,
                                  dataStore: TransientDataStore,
                                  eventBus: ViewEventBus,
                                  favoriteRepository: FavoriteRepository,
                                  animalRepository: AnimalRepository,
                                  resourceProvider: ResourceProvider): ViewModel {
        return FavoritesViewModel(listAdapter,
                animalListItemFactory,
                dataStore,
                eventBus,
                favoriteRepository,
                animalRepository,
                resourceProvider)
    }
}
