package com.javierruiz.shutterstocktestapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.javierruiz.shutterstocktestapp.R;
import com.javierruiz.shutterstocktestapp.model.ImageFile;
import com.javierruiz.shutterstocktestapp.module.network.file.FileDownloader;
import com.javierruiz.shutterstocktestapp.util.LogHelper;

/**
 * Created by Javier on 19.01.2016.
 */
public class ImageAdapter extends BaseAdapter {
    private ArrayList<ImageFile> mImages = new ArrayList<>();

    private FileDownloader mFileDownloader;

    public ImageAdapter(FileDownloader fileDownloader) {
        mFileDownloader = fileDownloader;
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

    private static class GridItemBindingHolder {
        private int itemPosition;
        private ImageView imageView;
        private ProgressWheel progressWheel;

        GridItemBindingHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
            progressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);
        }

    }
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View view, ViewGroup parent) {
        GridItemBindingHolder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_gridview, parent, false);

            holder = new GridItemBindingHolder(view);
            view.setTag(holder);
        } else {
            holder = (GridItemBindingHolder) view.getTag();
        }

        holder.itemPosition = position;
        ImageFile image = mImages.get(position);

        //invalidate current imageView
        holder.imageView.setImageResource(0);

        loadImageOrDownload(parent, image, holder, position);

        return view;
    }

    private void loadImageOrDownload(final View parent,
                                     final ImageFile imageFile,
                                     final GridItemBindingHolder holder,
                                     final int itemPosition) {

        if (imageFile.getFile().exists()) { // no problem
            loadImage(parent, imageFile, holder, itemPosition);
        } else {
            holder.progressWheel.setVisibility(View.VISIBLE);
            holder.progressWheel.spin();
            //Async
            mFileDownloader.downloadFile(imageFile.getUrl(), imageFile.getFile(),
                    new FileDownloader.Listener() {
                        @Override
                        public void progressUpdate(long transferred, long total) {
                            if (total > 0) {
                                if (holder.itemPosition == itemPosition) {
                                    float progress = transferred / (float) total;
                                    holder.progressWheel.setProgress(progress);
                                }
                            }
                        }

                        @Override
                        public void onError(IOException e) {
                            loadImage(parent, imageFile, holder, itemPosition);
                            LogHelper.logException(e);
                        }

                        @Override
                        public void onSuccess() {
                            loadImage(parent, imageFile, holder, itemPosition);
                        }
                    });
        }
    }
    private void loadImage(View parent,
                           ImageFile imageFile,
                           GridItemBindingHolder holder,
                           int itemPosition) {
        int gridItemSize = ((GridView) parent).getColumnWidth();
        Context context = parent.getContext();

        if (holder.itemPosition == itemPosition) { //check that view hasn't been recycled
            holder.progressWheel.setVisibility(View.GONE);
            File file = imageFile.getFile();
            if (file.exists()) {
                Picasso.with(context)
                        .load(file)
                        .resize(gridItemSize, gridItemSize)
                        .centerCrop()
                        .into(holder.imageView);
            } else { //show error pic
                Picasso.with(context)
                        .load(R.drawable.ic_error_outline_black_48dp)
                        .resize(gridItemSize, gridItemSize)
                        .centerCrop()
                        .into(holder.imageView);
            }
        }
    }


}
