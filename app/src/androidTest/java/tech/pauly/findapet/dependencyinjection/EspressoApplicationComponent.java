package tech.pauly.findapet.dependencyinjection;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import tech.pauly.findapet.discover.DiscoverFragmentTest;

@Singleton
@Component(modules = { AndroidSupportInjectionModule.class,
                       AndroidViewModule.class,
                       ApplicationModule.class,
                       DataModule.class,
                       EspressoModule.class})
public interface EspressoApplicationComponent extends ApplicationComponent {
    void inject(DiscoverFragmentTest test);
}
