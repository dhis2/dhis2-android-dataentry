package org.hisp.dhis.android.dataentry.home;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.View;

import io.reactivex.functions.Consumer;

interface HomeView extends View {

    @NonNull
    @UiThread
    Consumer<String> showUsername();

    @NonNull
    @UiThread
    Consumer<String> showUserInfo();

    @NonNull
    @UiThread
    Consumer<String> showUserInitials();
}
