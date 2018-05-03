package tech.pauly.findapet.shared;

import tech.pauly.findapet.dependencyinjection.EspressoApplicationComponent;
import tech.pauly.findapet.dependencyinjection.EspressoPetApplication;

import static android.support.test.InstrumentationRegistry.getTargetContext;

public class BaseEspressoTest {

    protected EspressoPetApplication getApplicationContext() {
        return (EspressoPetApplication) getTargetContext().getApplicationContext();
    }

    protected EspressoApplicationComponent getTestComponent() {
        return getApplicationContext().getComponent();
    }
}
