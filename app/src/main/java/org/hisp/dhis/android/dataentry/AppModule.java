package org.hisp.dhis.android.dataentry;

import android.app.Application;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class AppModule {
    private final Application application;
    private final String databaseName;

    public AppModule(@NonNull Application application, @NonNull String databaseName) {
        this.application = application;
        this.databaseName = databaseName;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }
}
