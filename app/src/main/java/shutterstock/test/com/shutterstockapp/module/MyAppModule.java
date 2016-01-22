package shutterstock.test.com.shutterstockapp.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import shutterstock.test.com.shutterstockapp.ShutterstockApp;

/**
 * Created by Javier on 22.01.2016.
 */
@Module
public class MyAppModule {
    private final ShutterstockApp app;

    public MyAppModule(ShutterstockApp app) {
        this.app = app;
    }

    @Provides @Singleton
    ShutterstockApp provideMyApp() {
        return app;
    }
}
