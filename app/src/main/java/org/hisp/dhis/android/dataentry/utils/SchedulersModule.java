package org.hisp.dhis.android.dataentry.utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SchedulersModule {

    @Provides
    @Singleton
    SchedulerProvider providesSchedulerProvider() {
        return new SchedulersProviderImpl();
    }
}
