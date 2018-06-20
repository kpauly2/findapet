package tech.pauly.findapet.dependencyinjection

import android.app.Application
import android.content.Context

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
internal class ApplicationModule(private val application: Application) {

    @Provides
    @ForApplication
    fun provideContext(): Context {
        return application
    }

    @Provides
    @IoScheduler
    fun provideIoScheduler(): Scheduler {
        return Schedulers.io()
    }

    @Provides
    @Singleton
    @MainThreadScheduler
    fun provideMainScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}
