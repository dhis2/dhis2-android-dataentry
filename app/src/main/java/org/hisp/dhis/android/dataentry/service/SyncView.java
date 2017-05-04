package org.hisp.dhis.android.dataentry.service;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import io.reactivex.functions.Consumer;

interface SyncView extends View {

    @NonNull
    Consumer<SyncResult> update();
}
