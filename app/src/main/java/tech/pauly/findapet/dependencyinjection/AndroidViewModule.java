package tech.pauly.findapet.dependencyinjection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import tech.pauly.findapet.discover.AnimalDetailsActivity;
import tech.pauly.findapet.discover.DiscoverFragment;
import tech.pauly.findapet.shared.MainActivity;

@Module
public abstract class AndroidViewModule {

    @ContributesAndroidInjector
    abstract DiscoverFragment bindDiscoverFragment();

    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector
    abstract AnimalDetailsActivity bindAnimalDetailsActivity();
}
