package tech.pauly.findapet.shared.datastore

import android.support.annotation.StringRes
import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import tech.pauly.findapet.BuildConfig
import tech.pauly.findapet.data.models.Animal
import tech.pauly.findapet.data.models.AnimalType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A Singleton class to handle data transfer and temporary storage between View Models.
 *
 * The first scenario in which this is useful is launching new screens. Instead of passing
 * data to the view layer and then back to the Presentation layer using the Intent API,
 * we can pass data directly between the two View Models in question. This uses the
 * [.save] API and the [.get] API.
 *
 * The objects that are saved into the TransientDataStore are UseCase objects, basically
 * just a wrapper object that denotes a specific use case. These are not to be reused,
 * every use case should have its own UseCase defined so that they don't wipe each other
 * when saving into the Map.
 *
 * The other scenario in which this class becomes useful is parallel View Model communication.
 * This is when there are multiple View Models living at the same time that need to
 * communicate with each other, but that don't have a reference to each other. They can use
 * the [.save] API and the [.observeUseCase] API to perform
 * this interaction. Note the recipient must observe before the sender saves for this to work.
 */
@Singleton
open class TransientDataStore @Inject constructor() {
    val transientData: ConcurrentMap<Class<*>, UseCase> = ConcurrentHashMap()
    val dataSubject: PublishSubject<Class<*>> = PublishSubject.create()

    open operator fun plusAssign(useCase: UseCase) {
        save(useCase)
    }

    @Deprecated("for Java use only, otherwise use +=")
    open fun save(useCase: UseCase) {
        val useCaseClass = useCase.javaClass

        if (BuildConfig.DEBUG && transientData.containsKey(useCaseClass)) {
            Log.e(javaClass.name, "Element of type $useCaseClass already exists")
        }

        transientData[useCaseClass] = useCase
        dataSubject.onNext(useCaseClass)
    }

    open operator fun <T : UseCase> get(useCaseClass: Class<T>): T? {
        if (BuildConfig.DEBUG && !containsUseCase(useCaseClass)) {
            Log.e(javaClass.name, "Expected element for use case " + useCaseClass.name)
        }

        val removedUseCase = transientData.remove(useCaseClass)
        return useCaseClass.cast(removedUseCase)
    }

    open fun <T : UseCase> observeUseCase(useCaseClass: Class<T>): Observable<Class<*>> {
        return dataSubject.filter { clazz -> clazz == useCaseClass }
    }

    open fun <T : UseCase> observeAndGetUseCase(useCaseClass: Class<T>): Observable<T> {
        return observeUseCase(useCaseClass).map { clazz -> get(clazz as Class<T>) }
    }

    open fun <T : UseCase> containsUseCase(useCaseClass: Class<T>): Boolean {
        return transientData.containsKey(useCaseClass)
    }
}

interface UseCase

open class FilterUpdatedUseCase : UseCase