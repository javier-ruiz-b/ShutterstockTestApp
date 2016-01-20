package shutterstock.test.com.shutterstockapp.network.retrofit;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

/**
 * Created by Javier on 19.01.2016.
 */
public class ShutterstockServiceBuilder {

    public static ShutterstockService build() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShutterstockService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ShutterstockService.class);
     }
}
