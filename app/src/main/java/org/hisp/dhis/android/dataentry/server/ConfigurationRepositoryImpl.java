package org.hisp.dhis.android.dataentry.server;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2;

import io.reactivex.Observable;

// ToDo: tests
class ConfigurationRepositoryImpl implements ConfigurationRepository {
    private final D2 d2;

    public ConfigurationRepositoryImpl(@NonNull D2 d2) {
        this.d2 = d2;
    }

    @NonNull
    @Override
    public Observable<Boolean> isUserLoggedIn() {
        return Observable.defer(() -> Observable.fromCallable(d2.isUserLoggedIn()));
    }
}
