package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

public interface SelectionNavigator {
    void navigateTo(@NonNull SelectionViewModel selectionViewModel);
}
