package tech.pauly.findapet.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import tech.pauly.findapet.di.authModule
import tech.pauly.findapet.di.petsModule

class PetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PetApplication)
            modules(listOf(authModule, petsModule))
        }
    }
}