package tech.pauly.findapet.dependencyinjection;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import tech.pauly.findapet.BuildConfig;

public class PetApplication extends Application implements HasActivityInjector,
                                                           HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidActivityInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidFragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        if (!BuildConfig.ENVIRONMENT.equals("espresso")) {
            LeakCanary.install(this);
        }

        DaggerApplicationComponent.builder()
                                  .applicationModule(new ApplicationModule(this))
                                  .build()
                                  .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidActivityInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidFragmentInjector;
    }
}
