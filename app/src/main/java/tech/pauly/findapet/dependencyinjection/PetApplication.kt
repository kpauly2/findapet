package tech.pauly.findapet.dependencyinjection

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import com.google.android.gms.maps.MapsInitializer
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import tech.pauly.findapet.BuildConfig
import javax.inject.Inject

open class PetApplication : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidActivityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingAndroidFragmentInjector: DispatchingAndroidInjector<Fragment>

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
                .build().also {
                    it.inject(this)
                }
        MapsInitializer.initialize(this)
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return dispatchingAndroidActivityInjector
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return dispatchingAndroidFragmentInjector
    }

    companion object {
        lateinit var component: ApplicationComponent
            private set
    }
}
