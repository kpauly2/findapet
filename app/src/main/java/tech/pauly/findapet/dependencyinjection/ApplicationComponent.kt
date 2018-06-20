package tech.pauly.findapet.dependencyinjection

import javax.inject.Singleton

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import tech.pauly.findapet.shared.PermissionHelper

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class,
    AndroidViewModule::class,
    ApplicationModule::class,
    DataModule::class])
interface ApplicationComponent : AndroidInjector<PetApplication> {
    fun permissionHelper(): PermissionHelper
}
