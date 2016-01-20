package shutterstock.test.com.shutterstockapp.network.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;
import shutterstock.test.com.shutterstockapp.model.json.SearchResponse;

/**
 * Created by Javier on 19.01.2016.
 */
public interface ShutterstockService {
    String URL = "https://api.shutterstock.com/v2/";

    @GET("images/search?")
    Call<SearchResponse> searchImages(@Header("Authorization") String auth,
                                      @Query("page") int page,
                                      @Query("per_page") int resultsPerPage,
                                      @Query("query") String query);


}
