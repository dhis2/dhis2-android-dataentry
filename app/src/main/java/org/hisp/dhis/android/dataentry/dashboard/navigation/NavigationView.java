package org.hisp.dhis.android.dataentry.dashboard.navigation;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

interface NavigationView extends View {

    @NonNull
    Observable<Object> createEventActions();

    @NonNull
    Consumer<String> navigateToCreateScreen();

    @NonNull
    Consumer<List<EventViewModel>> renderEvents();

    @NonNull
    Consumer<Pair<String, String>> renderAttributes();

    @NonNull
    Consumer<String> renderTitle();
}