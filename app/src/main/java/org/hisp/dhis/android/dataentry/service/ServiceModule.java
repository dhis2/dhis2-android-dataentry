package org.hisp.dhis.android.dataentry.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;

import org.hisp.dhis.android.dataentry.commons.dagger.PerService;

import dagger.Module;
import dagger.Provides;

@Module
@PerService
public class ServiceModule {

    @Provides
    @PerService
    NotificationManagerCompat notificationManager(@NonNull Context context) {
        return NotificationManagerCompat.from(context);
    }
}
