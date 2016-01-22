package shutterstock.test.com.shutterstockapp.model;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import shutterstock.test.com.shutterstockapp.model.json.Datum;
import shutterstock.test.com.shutterstockapp.model.json.ImageData;
import shutterstock.test.com.shutterstockapp.util.IOUtils;

/**
 * Created by Javier on 19.01.2016.
 */
public class ImageFile {

    private final static String IMAGE_FILE_SUFFIX = ".jpg";

    private Datum mImageInfo;
    private File mFile;
    private URL mUrl;

    public ImageFile(Context context, Datum imageInfo, ImageData preview)
            throws IOException {
        this(context, imageInfo, preview.width, preview.height, preview.url);
    }

    public ImageFile(Context context,
                     Datum mImageInfo,
                     Integer width,
                     Integer height,
                     String url) throws IOException {
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

    public Datum getImageInfo() {
        return mImageInfo;
    }
}

