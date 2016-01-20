package shutterstock.test.com.shutterstockapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import shutterstock.test.com.shutterstockapp.adapter.ImageAdapter;
import shutterstock.test.com.shutterstockapp.model.json.Datum;
import shutterstock.test.com.shutterstockapp.model.json.SearchResponse;
import shutterstock.test.com.shutterstockapp.network.retrofit.ShutterstockServiceClient;

public class MainActivity extends AppCompatActivity {

    private final static int IMAGES_PER_REQUEST = 10*3; //10 rows x 3 columns

    private int mLastRequestedPage = 1;
    private GridView mGridView;
    private ImageAdapter mGridViewAdapter;

    private PublishSubject<Datum> mSearchReponseObserver = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridViewAdapter = new ImageAdapter(getApplicationContext());
        mGridView.setAdapter(mGridViewAdapter);
    }


    private void searchImages(String name) {
        ShutterstockServiceClient client = ShutterstockServiceClient.getInstance(getApplicationContext());
        client.searchImages(mLastRequestedPage, IMAGES_PER_REQUEST, "apple")
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<SearchResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.i("SearchResponse", "Finish");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("SearchResponseErr", "Error" , e);
                    }

                    @Override
                    public void onNext(SearchResponse searchResponse) {
                        Log.i("SearchResponse", "Id: " + searchResponse.searchId +
                                "Page: " + searchResponse.page);
//                        mGridViewAdapter.addImages(searchResponse.getImages());

                        Observable<Datum> datumObservable = Observable.from(searchResponse.data);
                        datumObservable.subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Datum>() {
                                    @Override
                                    public void call(Datum datum) {
                                        Log.i("Datum", "Id: " + datum.id);
                                    }
                                });
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
