package org.hisp.dhis.android.dataentry.launcher;

import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.ui.View;

interface LauncherView extends View {

    @UiThread
    void navigateToLoginView();

    @UiThread
    void navigateToHomeView();
}