package com.javierruiz.shutterstocktestapp;

import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.javierruiz.shutterstocktestapp.model.json.SearchResponse;
import com.javierruiz.shutterstocktestapp.module.network.shutterstock.ShutterstockClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Javier on 24.01.2016.
 */
public class ShutterstockClientTest extends ApplicationTestCase<ShutterstockApp> {

    final int SEARCH_RESULTS_COUNT = 10;

    private ShutterstockClient client;

    public ShutterstockClientTest() {
        super(ShutterstockApp.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        client = getApplication().getComponent().shutterstockClient();
    }

    private void testResponse(Call<SearchResponse> call,
                              Response<SearchResponse> response,
                              int expectedResultsCount) {
        assertEquals(call.isExecuted(), true);
        assertEquals(call.isCanceled(), false);
        assertEquals(response.body().data.size(), expectedResultsCount);
    }

    @Test
    public void testSimpleSyncSearchCall() throws IOException {
        Call<SearchResponse> call = client.searchImages(1, SEARCH_RESULTS_COUNT, "test");
        Response<SearchResponse> response = call.execute();
        testResponse(call, response, SEARCH_RESULTS_COUNT);
    }

    @Test(timeout=5000)
    public void testSimpleAsyncSearchCall() throws IOException, InterruptedException {
        final int resultsCount = 10;
        final Call<SearchResponse> call = client.searchImages(1, resultsCount, "test");
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response) {
                testResponse(call, response, SEARCH_RESULTS_COUNT);
            }

            @Override
            public void onFailure(Throwable t) {
                assert (false);
            }
        });
        while (!call.isExecuted()) {
            wait(10);
        }
    }


    @Test
    public void testInvalidSyncSearchCall() throws IOException {
        int zeroResultsCount = -1;
        Call<SearchResponse> call = client.searchImages(1, zeroResultsCount, "test");
        Response<SearchResponse> response = call.execute();
        assertNull(response.body()); // expect null body
    }
}
