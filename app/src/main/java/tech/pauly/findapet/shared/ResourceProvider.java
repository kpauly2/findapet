package tech.pauly.findapet.shared;

import android.content.Context;
import android.support.annotation.StringRes;

import javax.inject.Inject;
import javax.inject.Singleton;

import tech.pauly.findapet.dependencyinjection.ForApplication;

@Singleton
public class ResourceProvider {

    private Context context;

    @Inject
    ResourceProvider(@ForApplication Context context) {
        this.context = context;
    }

    public String getString(@StringRes int stringId) {
        return context.getString(stringId);
    }
}
