package org.hisp.dhis.android.dataentry.launcher;

import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.Presenter;

interface LauncherPresenter extends Presenter {

    @UiThread
    void isUserLoggedIn();
}