package tech.pauly.findapet.dependencyinjection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import tech.pauly.findapet.discover.DiscoverFragment;

@Module
public abstract class AndroidViewModule {

    @ContributesAndroidInjector
    abstract DiscoverFragment bindDiscoverFragment();
}
