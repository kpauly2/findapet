package tech.pauly.findapet;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

import tech.pauly.findapet.dependencyinjection.EspressoPetApplication;

public class EspressoTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, EspressoPetApplication.class.getName(), context);
    }
}
