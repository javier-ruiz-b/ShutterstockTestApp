package shutterstock.test.com.shutterstockapp;

import android.app.Application;

import shutterstock.test.com.shutterstockapp.module.DaggerShutterstockAppComponent;
import shutterstock.test.com.shutterstockapp.module.ShutterstockAppComponent;
import shutterstock.test.com.shutterstockapp.module.network.FileDownloaderModule;
import shutterstock.test.com.shutterstockapp.module.network.shutterstock.ShutterstockClientModule;

/**
 * Created by Javier on 22.01.2016.
 */
public class ShutterstockApp extends Application {
    private ShutterstockAppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        
        component = DaggerShutterstockAppComponent.builder()
                .fileDownloaderModule(new FileDownloaderModule())
                .shutterstockClientModule(new ShutterstockClientModule(getApplicationContext()))
                .build();

        component.injectApp(this);

    }

    public ShutterstockAppComponent getComponent() {
        return component;
    }

}
