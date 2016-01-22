package shutterstock.test.com.shutterstockapp.module.network.shutterstock;

import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import shutterstock.test.com.shutterstockapp.model.json.SearchResponse;

/**
 * Created by Javier on 19.01.2016.
 */
@Singleton
public interface ShutterstockApiInterface {
    String URL = "https://api.shutterstock.com/v2/";

    @GET("images/search?")
    Call<SearchResponse> searchImages(@Header("Authorization") String auth,
                                      @Query("page") int page,
                                      @Query("per_page") int resultsPerPage,
                                      @Query("query") String query);


}
