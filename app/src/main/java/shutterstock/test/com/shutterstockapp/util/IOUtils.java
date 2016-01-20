package shutterstock.test.com.shutterstockapp.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Collection of functions which handle streams, files and folders.
 *
 * Created by Javier on 20.01.2016.
 */
public class IOUtils {

    private final static int BUFFER_SIZE = 4096;
    private final static String IMAGES_PATH = "images";

    private static File mImagesTempDir = null;

    //non-instantiable
    private IOUtils() {}

    public static File getAppCacheDir(Context context) {
        return context.getCacheDir();
    }

    //lazy load
    public static File getImagesTempDir(Context context) {
        if (mImagesTempDir == null) {
            mImagesTempDir = new File(getAppCacheDir(context), IMAGES_PATH);
        }
        return mImagesTempDir;
    }

    private static void createNoMediaFile(File path) throws IOException {
        if (path.isDirectory()) {
            new File(path, ".nomedia").createNewFile(); // fire and forget
        }
    }


    /**
     * Copy a file with a buffer.
     * See {@link IOUtils#copyStreams(InputStream, InputStream) copyStreams} for more information.
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    public static void copyFile(File inputFile,
                                File outputFile)
            throws IOException {
        FileInputStream fis = new FileInputStream(inputFile);
        FileOutputStream fos = new FileOutputStream(outputFile);

        copyStreams(fis, fos);

        fis.close();
        fos.close();
    }


    public static void moveFile(String inputPath, String outputPath) {
        moveFile(new File(inputPath), new File(outputPath));
    }

    /**
     * @param inputFile
     * @param outputFile
     * @return true on success
     */
    public static boolean moveFile(File inputFile, File outputFile) {
        return inputFile.renameTo(outputFile);
    }

    /**
     * Buffered copy. Buffer size determined by {@link IOUtils#BUFFER_SIZE}
     * @param in input stream
     * @param out output stream
     * @throws IOException
     */
    public static void copyStreams(InputStream in,
                                   OutputStream out)
            throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
