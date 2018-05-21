package tech.pauly.findapet.shared;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import tech.pauly.findapet.dependencyinjection.ApplicationComponent;
import tech.pauly.findapet.dependencyinjection.PetApplication;
import tech.pauly.findapet.shared.events.ActivityEvent;
import tech.pauly.findapet.shared.events.FragmentEvent;
import tech.pauly.findapet.shared.events.PermissionEvent;

public class BaseActivity extends AppCompatActivity {

    private CompositeDisposable lifecycleSubscriptions = new CompositeDisposable();
    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void fragmentEvent(FragmentEvent event) {
        Fragment newFragment = Fragment.instantiate(this, event.getFragmentClass().getName());
        getSupportFragmentManager().beginTransaction()
                                   .replace(event.getContainerId(), newFragment)
                                   .commit();
    }

    public void permissionEvent(PermissionEvent permissionEvent) {
        permissionHelper.requestPermission(this, permissionEvent);
    }

    @Nullable
    protected CompositeDisposable registerViewEvents() {
        return null;
    }

    protected void activityEvent(ActivityEvent event) {
        startActivity(new Intent(this, event.getStartActivityClass()));
    }

    protected void subscribeOnLifecycle(Disposable subscription) {
        lifecycleSubscriptions.add(subscription);
    }

    private void subscribeToEventBus() {
        lifecycleSubscriptions.clear();
        CompositeDisposable viewEvents = registerViewEvents();
        if (viewEvents != null) {
            lifecycleSubscriptions.add(viewEvents);
        }
    }
}
