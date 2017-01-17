package org.hisp.dhis.android.dataentry.server;

import android.support.annotation.NonNull;

import io.reactivex.Observable;

public interface ConfigurationRepository {
    @NonNull
    Observable<Boolean> isUserLoggedIn();
}
