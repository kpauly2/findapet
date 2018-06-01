package tech.pauly.findapet.shared;

import android.content.Context;
import android.support.test.espresso.idling.CountingIdlingResource;

import io.reactivex.Observable;
import tech.pauly.findapet.data.AnimalRepository;
import tech.pauly.findapet.data.AnimalService;
import tech.pauly.findapet.data.ObservableHelper;
import tech.pauly.findapet.data.models.AnimalListResponse;
import tech.pauly.findapet.data.models.AnimalType;

public class TestLocationHelper extends LocationHelper {

    private CountingIdlingResource countingIdlingResource;

    public TestLocationHelper(Context context, CountingIdlingResource countingIdlingResource) {
        super(context);
        this.countingIdlingResource = countingIdlingResource;
    }

    @Override
    public Observable<String> getCurrentLocation() {
        countingIdlingResource.increment();
        return super.getCurrentLocation().doAfterTerminate(() -> countingIdlingResource.decrement());
    }
}
