package tech.pauly.findapet.shared.datastore;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import tech.pauly.findapet.BuildConfig;

/**
 * A Singleton class to handle data transfer and temporary storage between View Models.
 *
 * The first scenario in which this is useful is launching new screens. Instead of passing
 * data to the view layer and then back to the Presentation layer using the Intent API,
 * we can pass data directly between the two View Models in question. This uses the
 * {@link #save(UseCase)} API and the {@link #get(Class)} API.
 *
 * The objects that are saved into the TransientDataStore are UseCase objects, basically
 * just a wrapper object that denotes a specific use case. These are not to be reused,
 * every use case should have its own UseCase defined so that they don't wipe each other
 * when saving into the Map.
 *
 * The other scenario in which this class becomes useful is parallel View Model communication.
 * This is when there are multiple View Models living at the same time that need to
 * communicate with each other, but that don't have a reference to each other. They can use
 * the {@link #save(UseCase)} API and the {@link #observeUseCase(Class)} API to perform
 * this interaction. Note the recipient must observe before the sender saves for this to work.
 */
@Singleton
public class TransientDataStore {
    private ConcurrentMap<Class, UseCase> transientData = new ConcurrentHashMap<>();
    private PublishSubject<Class> dataSubject = PublishSubject.create();

    @Inject
    TransientDataStore() {}

    public void save(UseCase useCase) {
        final Class<? extends UseCase> useCaseClass = useCase.getClass();

        if (BuildConfig.DEBUG && transientData.containsKey(useCaseClass)) {
            Log.e(getClass().getName(), "Element of type " + useCaseClass + " already exists");
        }

        transientData.put(useCaseClass, useCase);
        dataSubject.onNext(useCaseClass);
    }

    @Nullable
    public <T extends UseCase> T get(Class<T> useCaseClass) {
        if (BuildConfig.DEBUG && !containsUseCase(useCaseClass)) {
            Log.e(getClass().getName(), "Expected element for use case " + useCaseClass.getName());
        }

        final UseCase removedUseCase = transientData.remove(useCaseClass);
        return useCaseClass.cast(removedUseCase);
    }

    public <T extends UseCase> Observable<Class> observeUseCase(Class<T> useCaseClass) {
        return dataSubject.filter(clazz -> clazz.equals(useCaseClass));
    }

    @SuppressWarnings({ "ConstantConditions", "unchecked" })
    public <T extends UseCase> Observable<T> observeAndGetUseCase(Class<T> useCaseClass) {
        return observeUseCase(useCaseClass).flatMap(clazz -> Observable.just(get((Class<T>) clazz)));
    }

    public  <T extends UseCase> boolean containsUseCase(Class<T> useCaseClass) {
        return transientData.containsKey(useCaseClass);
    }

    @VisibleForTesting
    ConcurrentMap<Class, UseCase> getTransientData() {
        return transientData;
    }

    @VisibleForTesting
    PublishSubject<Class> getDataSubject() {
        return dataSubject;
    }
}
