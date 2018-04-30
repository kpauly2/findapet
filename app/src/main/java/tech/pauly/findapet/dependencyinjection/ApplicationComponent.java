package tech.pauly.findapet.dependencyinjection;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = { AndroidSupportInjectionModule.class,
                       AndroidViewModule.class,
                       DataModule.class })
public interface ApplicationComponent extends AndroidInjector<PetApplication> {

}
