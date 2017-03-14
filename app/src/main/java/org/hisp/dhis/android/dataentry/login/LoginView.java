package org.hisp.dhis.android.dataentry.login;

import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.ui.View;

interface LoginView extends View {
    @UiThread
    void showProgress();

    @UiThread
    void hideProgress();

    @UiThread
    void renderInvalidServerUrlError();

    @UiThread
    void renderInvalidCredentialsError();

    @UiThread
    void renderUnexpectedError();

    @UiThread
    void renderServerError();

    @UiThread
    void navigateToHome();
}
