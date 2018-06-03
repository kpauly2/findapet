package tech.pauly.findapet.data;

import android.support.annotation.VisibleForTesting;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;
import tech.pauly.findapet.data.models.Filter;

@Singleton
public class FilterRepository {

    private static long ABSENT = -1L;

    private FilterDatabase database;
    private ObservableHelper observableHelper;

    @VisibleForTesting
    Long currentFilterId = ABSENT;

    @Inject
    FilterRepository(FilterDatabase database, ObservableHelper observableHelper) {
        this.database = database;
        this.observableHelper = observableHelper;
    }

    public Single<Filter> getCurrentFilter() {
        if (currentFilterId == ABSENT) {
            return Single.error(new IllegalStateException("Can't get current filter as current filter ID wasn't set"));
        }
        return database.filterDao()
                       .findById(currentFilterId)
                       .compose(observableHelper.applySchedulers());
    }

    public Completable insertFilter(Filter filter) {
        return Single.fromCallable(() -> database.filterDao().insert(filter))
                     .map(id -> currentFilterId = id)
                     .compose(observableHelper.applySchedulers())
                     .ignoreElement();
    }

    public Single<Filter> getCurrentFilterAndNoFilterIfEmpty() {
        return getCurrentFilter().onErrorResumeNext(Single.just(new Filter()));
    }
}
