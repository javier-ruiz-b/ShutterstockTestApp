package com.javierruiz.shutterstocktestapp.module.network.file;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
