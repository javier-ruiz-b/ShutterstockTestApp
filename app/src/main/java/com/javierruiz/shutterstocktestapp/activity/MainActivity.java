package com.javierruiz.shutterstocktestapp.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import com.javierruiz.shutterstocktestapp.R;
import com.javierruiz.shutterstocktestapp.ShutterstockApp;
import com.javierruiz.shutterstocktestapp.adapter.ImageAdapter;
import com.javierruiz.shutterstocktestapp.model.ImageFile;
import com.javierruiz.shutterstocktestapp.model.json.ImageInfo;
import com.javierruiz.shutterstocktestapp.model.json.SearchResponse;
import com.javierruiz.shutterstocktestapp.module.network.file.FileDownloader;
import com.javierruiz.shutterstocktestapp.module.network.shutterstock.ShutterstockClient;
import com.javierruiz.shutterstocktestapp.util.LogHelper;

public class MainActivity extends AppCompatActivity {

    private final static int ROWS_PER_REQUEST = 12;
    private final static int COLUMNS = 3;
    private final static int IMAGES_PER_REQUEST = COLUMNS * ROWS_PER_REQUEST;

    //controls infinite load
    private boolean mReachedEnd;
    private int mLastRequestedPage;
    private String mSearchText;
    private boolean mLoadingMore;

    //Views
    private CoordinatorLayout mRootView;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ImageAdapter mGridViewAdapter;

    private PublishSubject<ImageFile> mShowImagesStream = PublishSubject.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FileDownloader fileDownloader = ((ShutterstockApp)getApplication()).getComponent().fileDownloader();

        mRootView = (CoordinatorLayout) findViewById(R.id.rootView);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE); // hidden by default

        ImageAdapter.Listener imageAdapterListener = new ImageAdapter.Listener() {
            @Override
            public void onImageClick(ImageFile image, View gridViewItem) {
                if (!image.getFile().exists()) {
                    return; // do not load if not ready
                }
                Intent intent = new Intent(MainActivity.this, FullscreenImageActivity.class);
                intent.putExtra(FullscreenImageActivity.KEY_PARAMETER_IMAGE_FILE, image);

                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(MainActivity.this, gridViewItem,
                                FullscreenImageActivity.KEY_COMMON_TRANSITION_VIEW);

                startActivity(intent, options.toBundle());
            }
        };

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridViewAdapter = new ImageAdapter(fileDownloader, imageAdapterListener);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setNumColumns(COLUMNS);

        //detect when scrolling to the end
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int loadBeforeItemsCount = IMAGES_PER_REQUEST / 2; // scrolled more than the half
                if (firstVisibleItem + visibleItemCount >= totalItemCount - loadBeforeItemsCount) {
                    // End has been reached
                    getNextImages(mSearchText);
                }
            }
        });

        //setup observable
        mShowImagesStream.buffer(COLUMNS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ImageFile>>() {
                    @Override
                    public void onCompleted() {
                        LogHelper.logEvent("mShowImagesStream: completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogHelper.logException(e);
                    }

                    @Override
                    public void onNext(List<ImageFile> imageFiles) {
                        mGridViewAdapter.addImages(imageFiles);
                    }

                });

    }

    private void searchImages(String searchText) {
        //initialize vars
        mLastRequestedPage = 1;
        mSearchText = searchText;
        mGridViewAdapter.clear();
        mGridView.scrollTo(0, 0); //scroll to top
        mReachedEnd = false;

        getNextImages(searchText);
    }

    private void stopProgressAnimation() {
        //has to be executed on main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

//    private final Subscriber<ImageInfo> mImageInfoSubscriber = new Subscriber<ImageInfo>() {
//        @Override
//        public void onCompleted() {
//            LogHelper.logEvent("completed");
//            stopProgressAnimation();
//        }
//
//        @Override
//        public void onError(Throwable e) {
//            LogHelper.logException(e);
//            stopProgressAnimation();
//        }
//
//        @Override
//        public void onNext(ImageInfo imageInfo) {
//            if (imageInfo.assets.preview != null) {
//                try {
//                    final ImageFile image =
//                            new ImageFile(getApplicationContext(),
//                                    imageInfo, imageInfo.assets.preview);
//                    mShowImagesStream.onNext(image);
//                } catch (IOException e) {
//                    LogHelper.logException(e);
//                }
//            }
//        }
//    };

    private void getNextImages(String searchText) {
        if (mReachedEnd || mLoadingMore) {
            return;
        }

        if (searchText == null) {
            return;
        }
        mLoadingMore = true;
        mProgressBar.setVisibility(View.VISIBLE);
        ShutterstockClient client = ((ShutterstockApp)getApplication()).getComponent().shutterstockClient();
        client.searchImages(mLastRequestedPage, IMAGES_PER_REQUEST, searchText).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                mReachedEnd = (searchResponse.data.size() != IMAGES_PER_REQUEST);
                if (mReachedEnd) {
                    Toast.makeText(getApplicationContext(), R.string.no_more_images, Toast.LENGTH_LONG).show();
                }

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
                                stopProgressAnimation();
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogHelper.logException(e);
                                stopProgressAnimation();
                            }

                            @Override
                            public void onNext(ImageInfo imageInfo) {
                                if (imageInfo.assets.preview != null) {
                                    try {
                                        final ImageFile image =
                                                new ImageFile(getApplicationContext(),
                                                        imageInfo, imageInfo.assets.preview);
                                        mShowImagesStream.onNext(image);
                                    } catch (IOException e) {
                                        LogHelper.logException(e);
                                    }
                                }
                            }
                        });

                mLoadingMore = false;
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(MainActivity.this, R.string.error_connection, Toast.LENGTH_LONG).show();
                stopProgressAnimation();
            }
        });

        mLastRequestedPage++;
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchImages(query);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(query);
                }
                //Exit search mode
                MenuItemCompat.collapseActionView(searchItem);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
