package org.hisp.dhis.android.dataentry.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2;

import io.reactivex.Observable;

// ToDo: Implement D2 in the way it can be tested easier
public class UserRepositoryImpl implements UserRepository {
    private final D2 d2;

    public UserRepositoryImpl(@NonNull D2 d2) {
        this.d2 = d2;
    }

    @Override
    public Observable<Boolean> isUserLoggedIn() {
        return Observable.defer(() -> Observable.fromCallable(d2.isUserLoggedIn()));
    }
}
