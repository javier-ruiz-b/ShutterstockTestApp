package shutterstock.test.com.shutterstockapp.network.retrofit;

import android.content.Context;
import android.util.Base64;

import rx.Observable;
import shutterstock.test.com.shutterstockapp.R;
import shutterstock.test.com.shutterstockapp.model.json.SearchResponse;

/**
 * Singleton. Allows communication to the Shutterstock API.
 *
 * Created by Javier on 20.01.2016.
 */
public class ShutterstockServiceClient {

    private ShutterstockService mShutterstockService;
    private String mAuthToken;

    private ShutterstockServiceClient(Context context) {
        mShutterstockService = ShutterstockServiceBuilder.build();
        mAuthToken = getAuthToken(context);
    }

    // Singleton
    private static ShutterstockServiceClient mInstance;
    public static ShutterstockServiceClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ShutterstockServiceClient(context);
        }
        return mInstance;
    }

    private String getAuthToken(Context context) {
        String token = context.getString(R.string.shutterstock_client_id) + ":" +
                context.getString(R.string.shutterstock_client_secret);
        return "Basic " + Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);
    }

    /**
     * Searches for pictures in the Shutterstock API
     * @param page number of page to show
     * @param resultsPerPage number of results per page
     * @param query search string
     * @return
     */
    public Observable<SearchResponse> searchImages(int page,
                                                   int resultsPerPage,
                                                   String query) {
        return mShutterstockService.searchImages(mAuthToken, page, resultsPerPage, query);
    }

}
