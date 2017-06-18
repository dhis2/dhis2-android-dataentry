package org.hisp.dhis.android.dataentry.selection;

import android.content.Intent;
import android.support.annotation.NonNull;

final class OnActivityResultNavigator implements SelectionNavigator {

    @NonNull
    private final SelectionDialogFragment selectionDialogFragment;

    OnActivityResultNavigator(@NonNull SelectionDialogFragment selectionDialogFragment) {
        this.selectionDialogFragment = selectionDialogFragment;
    }

    @Override
    public void navigateTo(@NonNull SelectionViewModel selectionViewModel) {
        Intent result = new Intent();
        result.putExtra(SelectionDialogFragment.SELECTION_RESULT, selectionViewModel);

        selectionDialogFragment.getTargetFragment().onActivityResult(
                selectionDialogFragment.getTargetRequestCode(),
                SelectionDialogFragment.RESULT_CODE, result);
        selectionDialogFragment.dismiss();
    }
}
