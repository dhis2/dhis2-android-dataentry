package org.hisp.dhis.android.dataentry.main;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.views.View;

import io.reactivex.functions.Consumer;

interface MainView extends View {

    @NonNull
    @UiThread
    Consumer<String> renderUsername();

    @NonNull
    @UiThread
    Consumer<String> renderUserInfo();

    @NonNull
    @UiThread
    Consumer<String> renderUserInitials();
}
