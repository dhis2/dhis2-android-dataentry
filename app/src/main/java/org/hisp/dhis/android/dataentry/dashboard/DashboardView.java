package org.hisp.dhis.android.dataentry.dashboard;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

interface DashboardView extends View {

    @NonNull
    Observable<Object> createEventActions();

    @NonNull
    Consumer<String> navigateToCreateScreen();

    @NonNull
    Consumer<List<EventViewModel>> renderEvents();

    @NonNull
    Consumer<Pair<String, String>> renderAttributes();
}