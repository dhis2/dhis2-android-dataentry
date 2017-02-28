package org.hisp.dhis.android.dataentry.utils;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SchedulerModule {
    private final SchedulerProvider schedulerProvider;

    public SchedulerModule(@NonNull SchedulerProvider schedulerProvider) {
        this.schedulerProvider = schedulerProvider;
    }

    @Provides
    @Singleton
    SchedulerProvider schedulerProvider() {
        return schedulerProvider;
    }
}
