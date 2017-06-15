package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.selection.SelectionViewModel;

import io.reactivex.Observable;

public interface CreateItemsView extends View {

    @NonNull
    @UiThread
    Observable<Object> selection1ClickEvents();

    @NonNull
    @UiThread
    Observable<Object> selection2ClickEvents();

    @NonNull
    @UiThread
    Observable<SelectionStateModel> selectionChanges(int id);

    @NonNull
    SelectionStateModel getSelectionState(int id);

    @NonNull
    @UiThread
    Observable<Object> selection1ClearEvent();


    @NonNull
    @UiThread
    Observable<Object> selection2ClearEvent();

    @UiThread
    void setSelection(int id, @NonNull String uid, @NonNull String name);

    @UiThread
    void showDialog1();

    @UiThread
    void showDialog2();

    @NonNull
    @UiThread
    Observable<Pair<String, String>> createButtonClick();

    @UiThread
    void navigateNext();

}
