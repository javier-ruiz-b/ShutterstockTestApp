package shutterstock.test.com.shutterstockapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shutterstock.test.com.shutterstockapp.model.ImageFile;

/**
 * Created by Javier on 19.01.2016.
 */
public class ImageAdapter extends BaseAdapter {
    private String TAG = "ImageAdapter";

    private Context mContext;
    private ArrayList<ImageFile> mImages = new ArrayList<>();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public void addImages(ImageFile[] images) {
        mImages.addAll(Arrays.asList(images));
        notifyDataSetChanged();
    }

    public void addImages(List<ImageFile> images) {
        mImages.addAll(images);
        notifyDataSetChanged();
    }

    public void addImage(ImageFile image) {
        mImages.add(image);
        notifyDataSetChanged();
    }

    public void clear() {
        mImages.clear();
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
            int size = ((GridView)parent).getColumnWidth();
            imageView.setLayoutParams(new GridView.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        ImageFile image = mImages.get(position);
        Picasso.with(mContext).load(image.getFile()).into(imageView);

        return imageView;
    }

}
