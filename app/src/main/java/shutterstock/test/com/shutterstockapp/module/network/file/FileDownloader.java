package shutterstock.test.com.shutterstockapp.module.network.file;

import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


import javax.inject.Singleton;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import shutterstock.test.com.shutterstockapp.util.IOUtils;

/**
 * Created by Javier on 20.01.2016.
 */
@Singleton
public class FileDownloader {

    private final static int BUFFER_SIZE = 2048;

    private OkHttpClient mClient;

    public FileDownloader() {
        mClient = new OkHttpClient();
    }

//    private static FileDownloader mInstance = null;
//    public static FileDownloader getInstance() {
//        if (mInstance == null) {
//            mInstance = new FileDownloader();
//        }
//        return mInstance;
//    }

    public void downloadFile(URL url,
                             final File outFile,
                             @Nullable final Listener listener) {
        final Request request = new Request.Builder()
                .url(url)
                .build();

        //enqueue download task
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (listener != null) {
                    listener.onError(e);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream is = response.body().byteStream();

                //create temporary file and move after download was successful
                File tempFile = new File(outFile.getAbsolutePath() + ".tmp");
                FileOutputStream os = new FileOutputStream(tempFile);
                long totalLength = response.body().contentLength();

                byte[] buffer = new byte[BUFFER_SIZE];
                long transferred = 0;
                int size;
                while ((size = is.read(buffer)) != -1) {
                    os.write(buffer, 0, size);
                    if (listener != null) {
                        transferred += size;
                        listener.progressUpdate(transferred, totalLength);
                    }
                }

                response.body().close();

                // download successful
                if (IOUtils.moveFile(tempFile, outFile)) {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                } else {
                    if (listener != null) {
                        listener.onError(new IOException("Can't move temporary file"));
                    }
                }
            }
        });
    }


    public interface Listener {
        /**
         *
         * @param transferred data transferred in bytes.
         * @param total total size in bytes. May be -1 if the entity doesn't provide contentLength.
         */
        void progressUpdate(long transferred, long total);

        void onError(IOException e);

        void onSuccess();
    }
}
