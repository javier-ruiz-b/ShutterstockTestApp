package shutterstock.test.com.shutterstockapp.module.network;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import shutterstock.test.com.shutterstockapp.module.network.file.FileDownloader;

/**
 * Created by Javier on 22.01.2016.
 */
@Module
public class FileDownloaderModule {

    @Provides @Singleton
    FileDownloader provideFileDownloader() {
        return new FileDownloader();
    }

}
