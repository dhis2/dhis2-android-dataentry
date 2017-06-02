package org.hisp.dhis.android.dataentry.selection;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = SelectionModule.class)
public interface SelectionComponent {

    void inject(SelectionDialogFragment fragment);

}
