package tech.pauly.findapet.shared;

import android.content.Context;
import android.support.test.espresso.idling.CountingIdlingResource;

import io.reactivex.Observable;

public class TestLocationHelper extends LocationHelper {

    private CountingIdlingResource countingIdlingResource;

    public TestLocationHelper(Context context, CountingIdlingResource countingIdlingResource) {
        super(context);
        this.countingIdlingResource = countingIdlingResource;
    }

    @Override
    public Observable<String> fetchCurrentLocation() {
        countingIdlingResource.increment();
        return super.fetchCurrentLocation().doAfterTerminate(() -> countingIdlingResource.decrement());
    }
}
