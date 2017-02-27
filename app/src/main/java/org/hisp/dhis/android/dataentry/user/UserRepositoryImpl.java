package org.hisp.dhis.android.dataentry.user;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.user.UserModel;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;

class UserRepositoryImpl implements UserRepository {
    private static final String SELECT = "SELECT * FROM " + UserModel.TABLE + " LIMIT 1";

    private final BriteDatabase briteDatabase;

    UserRepositoryImpl(@NonNull BriteDatabase briteDatabase) {
        this.briteDatabase = briteDatabase;
    }

    @NonNull
    @Override
    public Observable<UserModel> me() {
        return RxJavaInterop.toV2Observable(briteDatabase
                .createQuery(UserModel.TABLE, SELECT)
                .mapToOne(UserModel::create));
    }
}
