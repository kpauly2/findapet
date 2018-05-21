package tech.pauly.findapet.shared;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import tech.pauly.findapet.dependencyinjection.ApplicationComponent;
import tech.pauly.findapet.dependencyinjection.PetApplication;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.PermissionEvent;

public abstract class BaseFragment extends Fragment {

    private CompositeDisposable lifecycleSubscriptions = new CompositeDisposable();
    private PermissionHelper permissionHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationComponent component = PetApplication.getComponent();
        permissionHelper = component.permissionHelper();
    }

    @Override
    public void onResume() {
        subscribeToEventBus();
        super.onResume();
    }

    @Override
    public void onPause() {
        lifecycleSubscriptions.clear();
        super.onPause();
    }

    @Nullable
    protected CompositeDisposable registerViewEvents() {
        return null;
    }

    protected void activityEvent(ActivityEvent event) {
        startActivity(new Intent(getContext(), event.getStartActivityClass()));
    }

    public void permissionEvent(PermissionEvent permissionEvent) {
        permissionHelper.requestPermission(getActivity(), permissionEvent);
    }

    private void subscribeToEventBus() {
        lifecycleSubscriptions.clear();
        CompositeDisposable viewEvents = registerViewEvents();
        if (viewEvents != null) {
            lifecycleSubscriptions.add(viewEvents);
        }
    }
}
