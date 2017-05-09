package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.Presenter;

interface DataEntryPresenter extends Presenter {
    void showFields(@NonNull String uid);
}
