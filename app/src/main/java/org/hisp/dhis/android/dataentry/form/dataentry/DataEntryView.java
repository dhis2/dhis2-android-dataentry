package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;

import io.reactivex.Flowable;

interface DataEntryView extends View {

    @NonNull
    Flowable<RowAction> rowActions();
}
