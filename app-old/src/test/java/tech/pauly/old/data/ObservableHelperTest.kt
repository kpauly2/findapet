package tech.pauly.old.data

import io.mockk.every
import io.mockk.mockkStatic
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class ObservableHelperTest {

    private val ioScheduler = TestScheduler()
    private val mainThread = TestScheduler()
    private val subject = ObservableHelper()

    @Before
    fun setup() {
        mockkStatic("io.reactivex.schedulers.Schedulers")
        every { Schedulers.io() } returns ioScheduler
        mockkStatic("io.reactivex.android.schedulers.AndroidSchedulers")
        every { AndroidSchedulers.mainThread() } returns mainThread
    }

    @Test
    fun applyObservableSchedulers_appliesObservableSchedulers() {
        val subscriber = Observable.just("test")
                .compose(subject.applyObservableSchedulers())
                .test()

        subscriber.assertNoValues()
        ioScheduler.triggerActions()
        subscriber.assertNoValues()
        mainThread.triggerActions()
        subscriber.assertValue("test")
                .assertComplete()
    }

    @Test
    fun applySingleSchedulers_appliesSingleSchedulers() {
        val subscriber = Single.just("test")
                .compose(subject.applySingleSchedulers())
                .test()

        subscriber.assertNoValues()
        ioScheduler.triggerActions()
        subscriber.assertNoValues()
        mainThread.triggerActions()
        subscriber.assertValue("test")
                .assertComplete()
    }

    @Test
    fun applyCompletableSchedulers_appliesCompletableSchedulers() {
        val subscriber = Completable.fromAction {  }
                .compose(subject.applyCompletableSchedulers())
                .test()

        subscriber.assertNoValues()
        ioScheduler.triggerActions()
        subscriber.assertNoValues()
        mainThread.triggerActions()
        subscriber.assertComplete()
    }
}