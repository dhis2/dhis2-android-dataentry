package org.hisp.dhis.android.dataentry.selection;

import android.content.Intent;
import android.support.annotation.NonNull;

final class OnActivityResultNavigator implements SelectionNavigator {
    private static final String SELECTION_RESULT = "arg:selectionResult";
    private static final int RESULT_CODE = 0;

    @NonNull
    private final SelectionDialogFragment selectionDialogFragment;

    OnActivityResultNavigator(@NonNull SelectionDialogFragment selectionDialogFragment) {
        this.selectionDialogFragment = selectionDialogFragment;
    }

    @Override
    public void navigateTo(@NonNull SelectionViewModel selectionViewModel) {
        Intent result = new Intent();
        result.putExtra(SELECTION_RESULT, selectionViewModel);

        selectionDialogFragment.getTargetFragment().onActivityResult(
                selectionDialogFragment.getTargetRequestCode(), RESULT_CODE, result);
        selectionDialogFragment.dismiss();
    }
}
