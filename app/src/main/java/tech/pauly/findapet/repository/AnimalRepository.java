package tech.pauly.findapet.repository;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import tech.pauly.findapet.models.AnimalListResponse;

public class AnimalRepository {

    @Inject
    AnimalRepository() {}

    public Observable<AnimalListResponse> fetchAnimals() {
        return Observable.timer(5, TimeUnit.SECONDS)
                         .map(o -> new AnimalListResponse())
                         .observeOn(AndroidSchedulers.mainThread());
    }
}
