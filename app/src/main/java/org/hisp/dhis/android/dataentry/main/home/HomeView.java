package org.hisp.dhis.android.dataentry.main.home;

import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.View;

import java.util.List;

import io.reactivex.functions.Consumer;

public interface HomeView extends View {
    
    Consumer<List<HomeViewModel>> swapData();

    @UiThread
    void showError(String message);
}


