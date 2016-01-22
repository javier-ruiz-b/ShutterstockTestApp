package shutterstock.test.com.shutterstockapp.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.GridView;
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
import shutterstock.test.com.shutterstockapp.R;
import shutterstock.test.com.shutterstockapp.ShutterstockApp;
import shutterstock.test.com.shutterstockapp.adapter.ImageAdapter;
import shutterstock.test.com.shutterstockapp.model.ImageFile;
import shutterstock.test.com.shutterstockapp.model.json.Datum;
import shutterstock.test.com.shutterstockapp.model.json.SearchResponse;
import shutterstock.test.com.shutterstockapp.module.network.file.FileDownloader;
import shutterstock.test.com.shutterstockapp.module.network.shutterstock.ShutterstockClient;
import shutterstock.test.com.shutterstockapp.util.LogHelper;

public class MainActivity extends AppCompatActivity {

    private final static int ROWS_PER_REQUEST = 14;
    private final static int COLUMNS = 2;
    private final static int IMAGES_PER_REQUEST = COLUMNS * ROWS_PER_REQUEST;

    //controls infinite load
    private boolean mReachedEnd;
    private int mLastRequestedPage;
    private String mSearchText;
    private boolean mLoadingMore;

    private GridView mGridView;
    private ImageAdapter mGridViewAdapter;

    private PublishSubject<ImageFile> mShowImagesStream = PublishSubject.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridViewAdapter = new ImageAdapter(getApplicationContext());
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setNumColumns(COLUMNS);

        //detect when scrolling to the end
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int loadBeforeItemsCount = ROWS_PER_REQUEST * 2 / 3;
                if (firstVisibleItem + visibleItemCount >= totalItemCount - loadBeforeItemsCount) {
                    // End has been reached
                    getNextImages(mSearchText);
                }
            }
        });

        //setup observables
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

        // TODO: cancel last observables

        getNextImages(searchText);
    }

    private void getNextImages(String searchText) {
        if (mReachedEnd || mLoadingMore) {
            return;
        }

        if (searchText == null) {
            return;
        }
        mLoadingMore = true;
        ShutterstockClient client = ((ShutterstockApp)getApplication()).getComponent().shutterstockClient();
        client.searchImages(mLastRequestedPage, IMAGES_PER_REQUEST, searchText).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                mReachedEnd = (searchResponse.data.size() != IMAGES_PER_REQUEST);
                if (mReachedEnd) {
                    Toast.makeText(getApplicationContext(), R.string.no_more_images, Toast.LENGTH_LONG).show();
                }
                Observable<Datum> datumObservable = Observable.from(searchResponse.data);
                datumObservable.onBackpressureDrop()
                        .retry()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(Schedulers.computation())
                        .subscribe(new Subscriber<Datum>() {
                            @Override
                            public void onCompleted() {
                                LogHelper.logEvent("completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogHelper.logException(e);
                            }

                            @Override
                            public void onNext(Datum datum) {
                                if (datum.assets.preview != null) {
                                    try {
                                        final ImageFile image =
                                                new ImageFile(getApplicationContext(),
                                                        datum, datum.assets.preview);
                                        downloadImageIfNecessaryAndAdd(image);
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

            }
        });

        mLastRequestedPage++;
    }

    private void downloadImageIfNecessaryAndAdd(final ImageFile imageFile) {
        if (imageFile.getFile().exists()) {
            //great, no need to download
            mShowImagesStream.onNext(imageFile);
        } else {
            LogHelper.logEvent("Downloading " + imageFile.getFile().getName());
            //download
            FileDownloader fileDownloader = ((ShutterstockApp)getApplication()).getComponent().fileDownloader();
            fileDownloader.downloadFile(imageFile.getUrl(), imageFile.getFile(),
                    new FileDownloader.Listener() {
                        @Override
                        public void progressUpdate(long transferred, long total) {
                        }

                        @Override
                        public void onError(IOException e) {
                            LogHelper.logException(e);
                        }

                        @Override
                        public void onSuccess() {
                            mShowImagesStream.onNext(imageFile);
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        SearchManager searchManager = (SearchManager)
//                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

//        searchView.setSearchableInfo(searchManager.
//                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchImages(query);
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

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
