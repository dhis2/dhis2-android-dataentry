package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = SelectionModule.class)
public interface SelectionComponent {
    void inject(@NonNull SelectionDialogFragment fragment);
}
