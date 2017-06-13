package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import io.reactivex.Observable;

public interface CreateItemsView extends View {

    @NonNull
    Observable<CardViewActionModel> cardViewOneEvent();

    @NonNull
    Observable<CardViewActionModel> cardViewTwoEvent();

    void setCardViewsHintsEnrollment();

    /*

    @NonNull
    Consumer<Pair<String,String>> setCardViewOneText();

    @NonNull
    Consumer<Pair<String,String>> setCardViewTwoState();
*/

}
