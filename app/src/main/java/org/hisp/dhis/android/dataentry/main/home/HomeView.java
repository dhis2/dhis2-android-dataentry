package org.hisp.dhis.android.dataentry.main.home;

import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.functions.Consumer;

interface HomeView extends View {

    Consumer<List<HomeViewModel>> swapData();

    @UiThread
    void renderError(String message);
}