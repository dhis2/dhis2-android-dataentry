package org.hisp.dhis.android.dataentry;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationManagerFactory;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.dataentry.utils.SchedulerModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = SchedulerModule.class)
public final class AppModule {
    private final Application application;
    private final String databaseName;

    public AppModule(@NonNull Application application, @NonNull String databaseName) {
        this.application = application;
        this.databaseName = databaseName;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return application;
    }

    @Provides
    @Singleton
    DbOpenHelper providesDbOpenHelper(Context context) {
        return new DbOpenHelper(context, databaseName);
    }

    @Provides
    @Singleton
    ConfigurationManager providesConfigurationManager(DbOpenHelper dbOpenHelper) {
        return ConfigurationManagerFactory.create(dbOpenHelper);
    }
}
