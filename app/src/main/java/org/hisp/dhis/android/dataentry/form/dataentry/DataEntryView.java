package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.View;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

interface DataEntryView extends View {

    @NonNull
    Flowable<RowAction> rowActions();

    @NonNull
    Consumer<List<FieldViewModel>> showFields();
}
