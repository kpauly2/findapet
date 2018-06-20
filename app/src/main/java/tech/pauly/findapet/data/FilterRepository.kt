package tech.pauly.findapet.data

import android.support.annotation.VisibleForTesting
import io.reactivex.Completable
import io.reactivex.Single
import tech.pauly.findapet.data.models.Filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class FilterRepository @Inject
internal constructor(private val database: FilterDatabase,
                     private val observableHelper: ObservableHelper) {

    @VisibleForTesting
    internal var _currentFilter: Long? = null
    open val currentFilter
        get(): Single<Filter> {
            return if (_currentFilter == null) {
                Single.error(IllegalStateException("Can't get current filter as current filter ID wasn't set"))
            } else database.filterDao()
                    .findById(_currentFilter)
                    .compose(observableHelper.applySingleSchedulers())
        }

    open val currentFilterAndNoFilterIfEmpty: Single<Filter>
        get() = currentFilter.onErrorResumeNext(Single.just(Filter()))

    open fun insertFilter(filter: Filter): Completable {
        return Single.fromCallable { database.filterDao().insert(filter) }
                .map { id -> _currentFilter = id }
                .compose(observableHelper.applySingleSchedulers())
                .ignoreElement()
    }
}
