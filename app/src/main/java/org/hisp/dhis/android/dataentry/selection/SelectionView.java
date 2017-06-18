package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

interface SelectionView extends View {

    @NonNull
    Observable<SearchViewQueryTextEvent> searchView();

    @NonNull
    Observable<SelectionViewModel> searchResultClicks();

    @NonNull
    Consumer<List<SelectionViewModel>> renderSearchResults();

    @NonNull
    Consumer<String> renderTitle();

    @NonNull
    Consumer<SelectionViewModel> navigateTo();
}
