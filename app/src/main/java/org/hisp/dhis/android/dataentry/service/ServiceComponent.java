package org.hisp.dhis.android.dataentry.service;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.dagger.PerService;

import dagger.Subcomponent;

@PerService
@Subcomponent(modules = ServiceModule.class)
public interface ServiceComponent {
    void inject(@NonNull SyncService syncService);
}
