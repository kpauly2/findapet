package tech.pauly.findapet.dependencyinjection

import android.arch.lifecycle.ViewModelProvider
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import tech.pauly.findapet.shared.PermissionHelper
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,
    AndroidViewModule::class,
    ApplicationModule::class,
    DataModule::class,
    ViewModelModule::class])
interface ApplicationComponent : AndroidInjector<PetApplication> {
    fun permissionHelper(): PermissionHelper
    val viewModelFactory: ViewModelProvider.Factory
}
