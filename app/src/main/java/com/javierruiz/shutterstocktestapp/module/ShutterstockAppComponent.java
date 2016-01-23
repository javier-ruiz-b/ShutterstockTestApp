package com.javierruiz.shutterstocktestapp.module;

import javax.inject.Singleton;

import dagger.Component;
import com.javierruiz.shutterstocktestapp.ShutterstockApp;
import com.javierruiz.shutterstocktestapp.module.network.file.FileDownloaderModule;
import com.javierruiz.shutterstocktestapp.module.network.file.FileDownloader;
import com.javierruiz.shutterstocktestapp.module.network.shutterstock.ShutterstockClient;
import com.javierruiz.shutterstocktestapp.module.network.shutterstock.ShutterstockClientModule;

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
