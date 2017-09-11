package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryStoreModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        OptionSelectionModule.class, DataEntryStoreModule.class
})
public interface OptionSelectionComponent {
    void inject(@NonNull SelectionDialogFragment fragment);
}
