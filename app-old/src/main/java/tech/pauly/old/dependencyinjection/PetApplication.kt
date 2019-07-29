package tech.pauly.old.dependencyinjection

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.squareup.leakcanary.LeakCanary
import tech.pauly.old.BuildConfig

open class PetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        if (BuildConfig.ENVIRONMENT != "espresso") {
            LeakCanary.install(this)
        }

        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
        MapsInitializer.initialize(this)
    }

    companion object {
        lateinit var component: ApplicationComponent
            private set
    }
}
