package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.View;

import java.util.List;

import io.reactivex.functions.Consumer;

public interface SelectionView extends View {
    Consumer<List<SelectionViewModel>> update(@NonNull String title);
}
