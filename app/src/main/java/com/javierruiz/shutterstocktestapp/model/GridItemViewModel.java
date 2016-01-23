package com.javierruiz.shutterstocktestapp.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.javierruiz.shutterstocktestapp.BR;

/**
 * Created by Javier on 22.01.2016.
 */
public class GridItemViewModel extends BaseObservable {

    private float progressPercent;
    private boolean progressIndeterminate;

    private ImageFile imageFile;

    public GridItemViewModel(ImageFile imageFile, int position) {
        this.imageFile = imageFile;
    }

    public ImageFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    @Bindable
    public float getProgressPercent() {
        return progressPercent;
    }

    @Bindable
    public boolean isProgressIndeterminate() {
        return progressIndeterminate;
    }

    public void setProgressPercent(float progressPercent) {
        this.progressPercent = progressPercent;
        notifyPropertyChanged(BR.progressPercent);
    }

    public void setProgressIndeterminate(boolean progressIndeterminate) {
        this.progressIndeterminate = progressIndeterminate;
        notifyPropertyChanged(BR.progressIndeterminate);
    }

}
