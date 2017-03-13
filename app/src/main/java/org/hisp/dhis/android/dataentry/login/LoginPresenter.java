package org.hisp.dhis.android.dataentry.login;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.views.Presenter;

interface LoginPresenter extends Presenter {

    @UiThread
    void validateCredentials(@NonNull String serverUrl,
            @NonNull String username, @NonNull String password);
}
