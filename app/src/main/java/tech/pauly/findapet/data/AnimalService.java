package tech.pauly.findapet.data;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tech.pauly.findapet.data.models.AnimalListResponse;

public interface AnimalService {

    @GET("pet.find")
    Observable<AnimalListResponse> fetchAnimals(@Query("location") String location, @Query("key") String key);
}
