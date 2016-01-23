package com.javierruiz.shutterstocktestapp.module.network.shutterstock;

import android.content.Context;
import android.util.Base64;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import com.javierruiz.shutterstocktestapp.R;

/**
 * Singleton. Allows communication to the Shutterstock API.
 *
 * Created by Javier on 20.01.2016.
 */
@Module
public class ShutterstockClientModule {

    Context mContext;

    public ShutterstockClientModule(Context context) {
        mContext = context;
    }

    @Provides @Singleton
    ShutterstockApiInterface provideShutterstockApiInterface() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShutterstockApiInterface.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ShutterstockApiInterface.class);
    }

    @Provides @Singleton
    String provideAuthToken() {
        String token = mContext.getString(R.string.shutterstock_client_id) + ":" +
                mContext.getString(R.string.shutterstock_client_secret);
        return "Basic " + Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);
    }

    @Provides @Singleton
    ShutterstockClient provideShutterstockClient(ShutterstockApiInterface shutterstockApiInterface,
                                                 String authToken) {
        return new ShutterstockClient(shutterstockApiInterface, authToken);
    }

}
