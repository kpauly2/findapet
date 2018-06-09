package tech.pauly.findapet.data;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tech.pauly.findapet.data.models.BreedListResponse;

public interface BreedService {

    @GET("breed.list")
    Single<BreedListResponse> fetchBreeds(@Query("key") String key,
                                          @Query("animal") String type);
}
