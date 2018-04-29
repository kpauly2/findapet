package tech.pauly.findapet.data;

import javax.inject.Inject;

import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;

public class ObservableHelper {
    private final Scheduler ioScheduler;
    private final Scheduler mainThreadScheduler;

    @Inject
    public ObservableHelper(@IOScheduler Scheduler ioScheduler, @MainThreadScheduler Scheduler mainThreadScheduler) {
        this.ioScheduler = ioScheduler;
        this.mainThreadScheduler = mainThreadScheduler;
    }

    public <T> ObservableTransformer<T, T> applySchedulers() {
        return upstream -> upstream.subscribeOn(ioScheduler)
                                   .observeOn(mainThreadScheduler);
    }
}
