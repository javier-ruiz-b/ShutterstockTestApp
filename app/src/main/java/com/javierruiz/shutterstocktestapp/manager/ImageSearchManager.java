package com.javierruiz.shutterstocktestapp.manager;

import android.content.Context;
import android.os.Bundle;

import com.javierruiz.shutterstocktestapp.model.ImageFile;
import com.javierruiz.shutterstocktestapp.model.json.ImageInfo;
import com.javierruiz.shutterstocktestapp.model.json.SearchResponse;
import com.javierruiz.shutterstocktestapp.module.network.shutterstock.ShutterstockClient;
import com.javierruiz.shutterstocktestapp.util.LogHelper;

import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Javier on 23.01.2016.
 */
public class ImageSearchManager {

    private final int mResultsPerRequest;
    private final Listener mListener;
    private final Context mContext;
    private final ShutterstockClient mShutterstockClient;

    //controls infinite load
    private boolean mReachedEnd;
    private int mLastRequestedPage;
    private String mSearchText;

    private boolean mIsRequesting = false;

    private PublishSubject<ImageFile> mShowImagesStream = PublishSubject.create();

    public ImageSearchManager(Context context,
                              ShutterstockClient shutterstockClient,
                              int resultsPerRequest,
                              Listener listener) {
        mContext = context;
        mShutterstockClient = shutterstockClient;
        mResultsPerRequest = resultsPerRequest;
        mListener = listener;

        //setup observable
        mShowImagesStream
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImageFile>() {
                    @Override
                    public void onCompleted() {
                        LogHelper.logEvent("mShowImagesStream: completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogHelper.logException(e);
                        mListener.onFailure(e);
                    }

                    @Override
                    public void onNext(ImageFile imageFile) {
                        mListener.onNextItem(imageFile);
                    }

                });
    }

    public void requestNextImages() {
        getNextImages(mSearchText);
    }

    public void searchImages(String searchText) {
        //initialize vars
        mLastRequestedPage = 1;
        mSearchText = searchText;
        mReachedEnd = false;

        getNextImages(searchText);
    }


    private void getNextImages(String searchText) {
        if (mReachedEnd || mIsRequesting) {
            return;
        }

        if (searchText == null) {
            return;
        }

        mIsRequesting = true;
        mListener.onRequest();
        mShutterstockClient.searchImages(mLastRequestedPage, mResultsPerRequest, searchText)
                .enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Response<SearchResponse> response) {
                        SearchResponse searchResponse = response.body();
                        mReachedEnd = (searchResponse.data.size() != mResultsPerRequest);
                        if (mReachedEnd) {
                            mListener.onNoMoreImages();
                        }

                        mIsRequesting = false;

                        //process the response with RxJava
                        Observable<ImageInfo> imageInfoObservable = Observable.from(searchResponse.data);
                        imageInfoObservable.onBackpressureDrop()
                                .retry()
                                .subscribeOn(Schedulers.computation())
                                .observeOn(Schedulers.computation())
                                .subscribe(new Subscriber<ImageInfo>() {
                                    @Override
                                    public void onCompleted() {
                                        LogHelper.logEvent("completed");
                                        mLastRequestedPage++;
                                        mListener.onComplete();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        LogHelper.logException(e);
                                        mListener.onFailure(e);
                                    }

                                    @Override
                                    public void onNext(ImageInfo imageInfo) {
                                        if (imageInfo.assets.preview != null) {
                                            try {
                                                ImageFile image = new ImageFile(mContext, imageInfo,
                                                        imageInfo.assets.preview);
                                                mShowImagesStream.onNext(image);
                                            } catch (IOException e) {
                                                LogHelper.logException(e);
                                            }
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        mIsRequesting = false;
                        mListener.onFailure(t);
                    }
                });
    }

    private final static String KEY_PAGE = "page";
    private final static String KEY_SEARCH_STRING = "searchString";
    private final static String KEY_REACHED_END = "reachedEnd";

    public void saveState(Bundle bundle) {
        bundle.putInt(KEY_PAGE, mLastRequestedPage);
        bundle.putString(KEY_SEARCH_STRING, mSearchText);
        bundle.putBoolean(KEY_REACHED_END, mReachedEnd);
    }

    public void restoreState(Bundle bundle) {
        mLastRequestedPage = bundle.getInt(KEY_PAGE);
        mSearchText = bundle.getString(KEY_SEARCH_STRING);
        mReachedEnd = bundle.getBoolean(KEY_REACHED_END);
    }

    public interface Listener {
        void onRequest();
        void onComplete();
        void onNoMoreImages();
        void onFailure(Throwable t);
        void onNextItem(ImageFile imageFile);
    }

}
