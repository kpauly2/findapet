package tech.pauly.findapet.shared;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseActivity extends AppCompatActivity {

    private CompositeDisposable lifecycleSubscriptions = new CompositeDisposable();

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

    public void fragmentEvent(FragmentEvent event) {
        Fragment newFragment = Fragment.instantiate(this, event.getFragmentClass().getName());
        getSupportFragmentManager().beginTransaction()
                                   .replace(event.getContainerId(), newFragment)
                                   .commit();
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
