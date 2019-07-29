package tech.pauly.old.dependencyinjection

import android.app.Application
import android.content.Context

import dagger.Module
import dagger.Provides

@Module
internal class ApplicationModule(private val application: Application) {

    @Provides
    @ForApplication
    fun provideContext(): Context {
        return application
    }
}
