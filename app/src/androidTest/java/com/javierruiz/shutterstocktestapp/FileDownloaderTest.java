package com.javierruiz.shutterstocktestapp;

import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;

import com.javierruiz.shutterstocktestapp.module.network.file.FileDownloader;
import com.javierruiz.shutterstocktestapp.util.IOUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Javier on 24.01.2016.
 */
public class FileDownloaderTest extends ApplicationTestCase<ShutterstockApp> {

    private FileDownloader downloader;
    private File testFile;

    public FileDownloaderTest() {
        super(ShutterstockApp.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        downloader = getApplication().getComponent().fileDownloader();
        testFile = new File(IOUtils.getAppCacheDir(getSystemContext()), "fileDownloader.tmp");
        deleteFile();
    }

    private void deleteFile() {
        if (testFile.exists()) {
            assertTrue(testFile.delete());
            assertFalse(testFile.exists());
        }
    }

    @Test(timeout=5000)
    public void testDownload() throws IOException, InterruptedException {
        URL url = new URL("http://www.google.de");
        Looper.prepare();
        okhttp3.Call call = downloader.downloadFile(url, testFile, new FileDownloader.Listener() {
            @Override
            public void progressUpdate(long transferred, long total) {
            }

            @Override
            public void onError(IOException e) {
                assertFalse(true);
                deleteFile();
            }

            @Override
            public void onSuccess() {
                assertTrue(testFile.exists());
                deleteFile();
            }
        });

        while (!call.isExecuted()) {
            wait(10);
        }
    }

//    @After
//    public void tearDown() throws Exception {
//        super.tearDown();
//        deleteFile();
//    }
}
