package tech.pauly.findapet.shared.datastore

import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import tech.pauly.findapet.BuildConfig
import tech.pauly.findapet.shared.isDebug
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

/**
 * A Singleton class to handle data transfer and temporary storage between View Models.
 *
 * The first scenario in which this is useful is launching new screens. Instead of passing
 * data to the view layer and then back to the Presentation layer using the Intent API,
 * we can pass data directly between the two View Models in question. This uses the
 * [.plusAssign] API and the [.get] API.
 *
 * The objects that are saved into the TransientDataStore are UseCase objects, basically
 * just a wrapper object that denotes a specific use case. These are not to be reused,
 * every use case should have its own UseCase defined so that they don't wipe each other
 * when saving into the Map.
 *
 * The other scenario in which this class becomes useful is parallel View Model communication.
 * This is when there are multiple View Models living at the same time that need to
 * communicate with each other, but that don't have a reference to each other. They can use
 * the [.plusAssign] API and the [.observeUseCase] API to perform
 * this interaction.
 */
@Singleton
open class TransientDataStore @Inject constructor() {
    val transientData: ConcurrentMap<KClass<*>, UseCase> = ConcurrentHashMap()
    val dataSubject: BehaviorSubject<KClass<*>> = BehaviorSubject.create()

    open operator fun plusAssign(useCase: UseCase) {
        val useCaseClass = useCase::class

        if (isDebug() && transientData.containsKey(useCaseClass)) {
            Log.e(javaClass.name, "Element of type $useCaseClass already exists")
        }

        transientData[useCaseClass] = useCase
        dataSubject.onNext(useCaseClass)
    }

    open operator fun <T : UseCase> get(useCaseClass: KClass<T>): T? {
        if (isDebug() && !containsUseCase(useCaseClass)) {
            Log.e(javaClass.name, "Expected element for use case " + useCaseClass.simpleName)
        }

        val removedUseCase = transientData.remove(useCaseClass)
        return if (removedUseCase == null) null else useCaseClass.cast(removedUseCase)
    }

    open fun <T : UseCase> observeUseCase(useCaseClass: KClass<T>): Observable<KClass<*>> {
        return dataSubject.filter { clazz -> clazz == useCaseClass }
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T : UseCase> observeAndGetUseCase(useCaseClass: KClass<T>): Observable<T> {
        return observeUseCase(useCaseClass).map { clazz -> get(clazz as KClass<T>) }
    }

    open fun <T : UseCase> containsUseCase(useCaseClass: KClass<T>): Boolean {
        return transientData.containsKey(useCaseClass)
    }
}

interface UseCase

data class FilterUpdatedUseCase(private val id: Int = 0) : UseCase