package tech.pauly.findapet.data;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tech.pauly.findapet.data.models.AnimalListResponse;

public interface AnimalService {

    @GET("pet.find")
    Single<AnimalListResponse> fetchAnimals(@Query("location") String location,
                                            @Query("key") String key,
                                            @Query("animal") String type,
                                            @Query("offset") int offset,
                                            @Query("count") int count,
                                            @Query("sex") String sex);
}
