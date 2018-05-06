package tech.pauly.findapet.shared;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel implements LifecycleObserver {

    private CompositeDisposable lifecycleSubscriptions = new CompositeDisposable();

    protected void subscribeOnLifecycle(Disposable subscription) {
        lifecycleSubscriptions.add(subscription);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void disposeSubscriptions() {
        lifecycleSubscriptions.clear();
    }
}
