package shutterstock.test.com.shutterstockapp.module.network.shutterstock;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import shutterstock.test.com.shutterstockapp.model.json.SearchResponse;

/**
 * Created by Javier on 22.01.2016.
 */
@Singleton
public class ShutterstockClient {
    private final ShutterstockApiInterface mApiInterface;
    private final String mAuthToken;

    @Inject
    public ShutterstockClient(ShutterstockApiInterface apiInterface, String authToken) {
        mApiInterface = apiInterface;
        mAuthToken = authToken;
    }

    /**
     * Searches for pictures in the Shutterstock API
     * @param page number of page to show
     * @param resultsPerPage number of results per page
     * @param query search string
     * @return
     */
    public Call<SearchResponse> searchImages(int page,
                                             int resultsPerPage,
                                             String query) {
        return mApiInterface.searchImages(mAuthToken, page, resultsPerPage, query);
    }
}
