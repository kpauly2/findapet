package tech.pauly.findapet.shared;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;
import javax.inject.Singleton;

import tech.pauly.findapet.dependencyinjection.ForApplication;


/**
 * A Singleton class to handle things that need context.
 *
 * Not an ideal solution for sure, but I continue to run into situations where
 * there's no way around using context in a ViewModel. Usually when concatenating
 * local resources and Strings from server data. This is a god object that holds one
 * reference to the Application. Not an ideal solution, but everyone can inject
 * this class and use the limited set of context APIs implemented in this class.
 */
@Singleton
public class ContextProvider {

    private Context context;

    @Inject
    ContextProvider(@ForApplication Context context) {
        this.context = context;
    }

    public String getString(@StringRes int stringId) {
        return context.getString(stringId);
    }

    public boolean hasPermission(String... permissions) {
        boolean hasPermission = true;
        for (String permission : permissions) {
            hasPermission &= ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return hasPermission;
    }
}
