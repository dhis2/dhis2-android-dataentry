package org.hisp.dhis.android.dataentry.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.user.UserModel;

import io.reactivex.Observable;

public interface UserRepository {
    @NonNull
    Observable<UserModel> me();
}
