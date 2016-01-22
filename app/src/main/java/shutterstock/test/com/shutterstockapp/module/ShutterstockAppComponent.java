package shutterstock.test.com.shutterstockapp.module;

import javax.inject.Singleton;

import dagger.Component;
import shutterstock.test.com.shutterstockapp.ShutterstockApp;
import shutterstock.test.com.shutterstockapp.module.network.FileDownloaderModule;
import shutterstock.test.com.shutterstockapp.module.network.file.FileDownloader;
import shutterstock.test.com.shutterstockapp.module.network.shutterstock.ShutterstockClient;
import shutterstock.test.com.shutterstockapp.module.network.shutterstock.ShutterstockClientModule;

/**
 * Created by Javier on 22.01.2016.
 */
@Singleton
@Component(
    modules = {
            FileDownloaderModule.class,
            ShutterstockClientModule.class
    }
)

public interface ShutterstockAppComponent {
    FileDownloader fileDownloader();
    ShutterstockClient shutterstockClient();

    void injectApp(ShutterstockApp shutterstockApp);
}
