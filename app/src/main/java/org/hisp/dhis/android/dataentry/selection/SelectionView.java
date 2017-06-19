package org.hisp.dhis.android.dataentry.selection;

import com.jakewharton.rxbinding2.support.v7.widget.SearchViewQueryTextEvent;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public interface SelectionView extends View {
    Consumer<List<SelectionViewModel>> update();

    void setTitle(String title);

    Observable<SearchViewQueryTextEvent> subscribeToSearchView();
}
