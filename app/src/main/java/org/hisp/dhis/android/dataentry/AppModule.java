package org.hisp.dhis.android.dataentry;

import android.content.Context;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationManagerFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hu.supercluster.paperwork.Paperwork;

@Module
final class AppModule {
    private final DhisApp application;

    AppModule(@NonNull DhisApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context context() {
        return application;
    }

    @Provides
    @Singleton
    Components application() {
        return application;
    }

    @Provides
    @Singleton
    Paperwork paperwork(Context context) {
        return new Paperwork(context);
    }

    @Provides
    @Singleton
    ConfigurationManager configurationManager(DatabaseAdapter databaseAdapter) {
        return ConfigurationManagerFactory.create(databaseAdapter);
    }

    @Provides
    @Singleton
    ConfigurationRepository configurationRepository(ConfigurationManager configurationManager) {
        return new ConfigurationRepositoryImpl(configurationManager);
    }
}
