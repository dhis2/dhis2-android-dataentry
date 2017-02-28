package org.hisp.dhis.android.dataentry.login;

import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.View;

interface LoginView extends View {
    @UiThread
    void showProgress();

    @UiThread
    void hideProgress();

    @UiThread
    void showInvalidServerUrlError();

    @UiThread
    void showInvalidCredentialsError();

    @UiThread
    void showUnexpectedError();

    @UiThread
    void showServerError();

    @UiThread
    void navigateToHome();
}
