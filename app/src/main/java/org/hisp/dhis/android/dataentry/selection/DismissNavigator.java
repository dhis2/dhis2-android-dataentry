package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

final class DismissNavigator implements SelectionNavigator {

    @NonNull
    private final SelectionDialogFragment selectionDialogFragment;

    DismissNavigator(@NonNull SelectionDialogFragment selectionDialogFragment) {
        this.selectionDialogFragment = selectionDialogFragment;
    }

    @Override
    public void navigateTo(@NonNull SelectionViewModel selectionViewModel) {
        selectionDialogFragment.dismiss();
    }
}
