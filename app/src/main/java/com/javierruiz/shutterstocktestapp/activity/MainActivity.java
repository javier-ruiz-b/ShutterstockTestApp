package com.javierruiz.shutterstocktestapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.javierruiz.shutterstocktestapp.R;
import com.javierruiz.shutterstocktestapp.ShutterstockApp;
import com.javierruiz.shutterstocktestapp.adapter.ImageAdapter;
import com.javierruiz.shutterstocktestapp.manager.ImageSearchManager;
import com.javierruiz.shutterstocktestapp.model.ImageFile;
import com.javierruiz.shutterstocktestapp.module.network.file.FileDownloader;
import com.javierruiz.shutterstocktestapp.module.network.shutterstock.ShutterstockClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static int ROWS_PER_REQUEST = 12;
    private final static int COLUMNS = 3;
    private final static int IMAGES_PER_REQUEST = COLUMNS * ROWS_PER_REQUEST;

    private final static String KEY_IMAGE_FILES = "image_files";
    private final static String KEY_SCROLL_X = "scroll_x";

    //Views
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private ImageAdapter mGridViewAdapter;

    private ImageSearchManager mImageSearchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShutterstockClient shutterstockClient = ((ShutterstockApp)getApplication()).getComponent().shutterstockClient();
        FileDownloader fileDownloader = ((ShutterstockApp)getApplication()).getComponent().fileDownloader();

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

        //initialize the search manager
        mImageSearchManager = new ImageSearchManager(getApplicationContext(),
                shutterstockClient,
                IMAGES_PER_REQUEST,
                new ImageSearchManager.Listener() {
                    @Override
                    public void onRequest() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNoMoreImages() {
                        Toast.makeText(MainActivity.this, R.string.no_more_images, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        stopProgressAnimation();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        stopProgressAnimation();
                        Toast.makeText(MainActivity.this, getString(R.string.error_connection),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNextItem(ImageFile imageFile) {
                        mGridViewAdapter.addImage(imageFile);
                    }
                });

        //detect when scrolling to the end
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int loadBeforeItemsCount = IMAGES_PER_REQUEST / 2; // scrolled more than the half
                if (firstVisibleItem + visibleItemCount >= totalItemCount - loadBeforeItemsCount) {
                    mImageSearchManager.requestNextImages();
//                    getNextImages(mSearchText);
                }
            }
        });

        if (savedInstanceState != null) {
            mImageSearchManager.restoreState(savedInstanceState);
            ArrayList<ImageFile> imageFiles = (ArrayList<ImageFile>) savedInstanceState.getSerializable(KEY_IMAGE_FILES);
            mGridViewAdapter.addImages(imageFiles);
            int scrollX = savedInstanceState.getInt(KEY_SCROLL_X);
            mGridView.scrollTo(scrollX, 0);
        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mImageSearchManager.saveState(outState);
        ArrayList<ImageFile> imageFiles = mGridViewAdapter.getImages();
        outState.putSerializable(KEY_IMAGE_FILES, imageFiles);
        outState.putInt(KEY_SCROLL_X, mGridView.getScrollX());
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
                mGridViewAdapter.clear();
                mGridView.scrollTo(0, 0); //scroll to top
                mImageSearchManager.searchImages(query);
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
