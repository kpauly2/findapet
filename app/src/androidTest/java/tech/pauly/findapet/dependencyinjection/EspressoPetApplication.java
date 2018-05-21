package tech.pauly.findapet.dependencyinjection;

public class EspressoPetApplication extends PetApplication {

    private EspressoApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerEspressoApplicationComponent.builder()
                                  .applicationModule(new ApplicationModule(this))
                                  .build();
        component.inject(this);
    }

    public EspressoApplicationComponent getEspressoComponent() {
        return component;
    }
}
