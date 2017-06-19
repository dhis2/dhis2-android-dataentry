package org.hisp.dhis.android.dataentry.commons.utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public final class UtilsModule {

    @Provides
    @Singleton
    CurrentDateProvider currentDateProvider() {
        return new CurrentDateProviderImpl();
    }
}
