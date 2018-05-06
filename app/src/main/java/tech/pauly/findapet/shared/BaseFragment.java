package tech.pauly.findapet.shared;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseFragment extends Fragment {

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

    @Nullable
    protected CompositeDisposable registerViewEvents() {
        return null;
    }

    protected void activityEvent(ActivityEvent event) {
        switch (event.getType()) {
            case START:
                startActivity(new Intent(getContext(), event.getStartActivityClass()));
                return;
            case FINISH:
                getActivity().finish();
                return;
            default:
                throw new IllegalStateException("ActivityEvent type " + event.getType() + " not handled.");
        }
    }

    private void subscribeToEventBus() {
        lifecycleSubscriptions.clear();
        CompositeDisposable viewEvents = registerViewEvents();
        if (viewEvents != null) {
            lifecycleSubscriptions.add(viewEvents);
        }
    }
}
