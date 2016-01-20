package shutterstock.test.com.shutterstockapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;

import shutterstock.test.com.shutterstockapp.model.ImageFileFromUrl;

/**
 * Created by Javier on 19.01.2016.
 */
public class ImageAdapter extends BaseAdapter {
    private String TAG = "ImageAdapter";

    private Context mContext;
    private ArrayList<ImageFileFromUrl> mImages = new ArrayList<>();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void addImages(ImageFileFromUrl[] images) {
        mImages.addAll(Arrays.asList(images));
        notifyDataSetChanged();
    }

    public void addImage(ImageFileFromUrl image) {
        mImages.add(image);
        notifyDataSetChanged();
    }

    public int getCount() {
        return mImages.size();
    }

    public Object getItem(int position) {
        return mImages.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        ImageFileFromUrl image = mImages.get(position);
//        try {
//            Picasso.with(mContext).load(image.getSmallThumbnailUrl()).into(imageView);
//        } catch (MalformedURLException e) {
//            Log.e(ImageAdapter, )
//        }

        return imageView;
    }

}
