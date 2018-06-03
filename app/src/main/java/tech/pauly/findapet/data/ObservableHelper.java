package tech.pauly.findapet.data;

import javax.inject.Inject;

import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.SingleTransformer;
import tech.pauly.findapet.dependencyinjection.IoScheduler;
import tech.pauly.findapet.dependencyinjection.MainThreadScheduler;

public class ObservableHelper {
    private final Scheduler ioScheduler;
    private final Scheduler mainThreadScheduler;

    @Inject
    public ObservableHelper(@IoScheduler Scheduler ioScheduler, @MainThreadScheduler Scheduler mainThreadScheduler) {
        this.ioScheduler = ioScheduler;
        this.mainThreadScheduler = mainThreadScheduler;
    }

    public <T> ObservableTransformer<T, T> applyObservableSchedulers() {
        return upstream -> upstream.subscribeOn(ioScheduler)
                                   .observeOn(mainThreadScheduler);
    }

    public <T> SingleTransformer<T, T> applySingleSchedulers() {
        return upstream -> upstream.subscribeOn(ioScheduler)
                                   .observeOn(mainThreadScheduler);
    }
}
