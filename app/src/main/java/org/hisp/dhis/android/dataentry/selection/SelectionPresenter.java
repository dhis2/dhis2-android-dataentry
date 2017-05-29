package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.ui.Presenter;
import org.hisp.dhis.android.dataentry.commons.ui.View;

public interface SelectionPresenter extends Presenter {
    void onAttach(@NonNull View view, @NonNull String query);
}
