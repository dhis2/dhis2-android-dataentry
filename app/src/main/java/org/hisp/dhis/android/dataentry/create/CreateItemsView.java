package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import io.reactivex.Observable;

public interface CreateItemsView extends View {

    @NonNull
    @UiThread
    Observable<Object> cardViewClickEvent(int index);

    @NonNull
    @UiThread
    Observable<Object> cardViewClearEvent(int index);

    @NonNull
    @UiThread
    Observable<Object> createButtonEvent();

    @UiThread
    void setCardViewText(int id, @NonNull String text);

    @UiThread
    void setCardViewsHintsEnrollment();

    @UiThread
    void showDialog(int id);

    @UiThread
    void createItem();
}
