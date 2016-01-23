package com.javierruiz.shutterstocktestapp;

import android.app.Application;

import com.javierruiz.shutterstocktestapp.module.DaggerShutterstockAppComponent;
import com.javierruiz.shutterstocktestapp.module.ShutterstockAppComponent;
import com.javierruiz.shutterstocktestapp.module.network.file.FileDownloaderModule;
import com.javierruiz.shutterstocktestapp.module.network.shutterstock.ShutterstockClientModule;

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
