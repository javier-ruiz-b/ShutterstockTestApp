package com.javierruiz.shutterstocktestapp.model;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import com.javierruiz.shutterstocktestapp.model.json.ImageInfo;
import com.javierruiz.shutterstocktestapp.model.json.ImageData;
import com.javierruiz.shutterstocktestapp.util.IOUtils;

/**
 * Created by Javier on 19.01.2016.
 */
public class ImageFile implements Serializable {

    private final static String IMAGE_FILE_SUFFIX = ".jpg";

    private ImageInfo mImageInfo;
    private File mFile;
    private URL mUrl;

    public ImageFile(Context context, ImageInfo imageInfo, ImageData preview)
            throws IOException {
        this(context, imageInfo, preview.width, preview.height, preview.url);
    }

    public ImageFile(Context context,
                     ImageInfo imageInfo,
                     Integer width,
                     Integer height,
                     String url) throws IOException {
        mImageInfo = imageInfo;
        String name = mImageInfo.id + "_" + width.toString() + "x" + height.toString() + IMAGE_FILE_SUFFIX;
        File imagesFolder = IOUtils.getImagesTempDir(context);
        mFile = new File(imagesFolder, name);
        mUrl = new URL(url);
    }

    public File getFile() {
        return mFile;
    }

    public URL getUrl() {
        return mUrl;
    }

    public ImageInfo getImageInfo() {
        return mImageInfo;
    }
}

