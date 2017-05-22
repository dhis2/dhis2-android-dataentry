package org.hisp.dhis.android.dataentry.selection;

import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = SelectionModule.class)
public interface SelectionComponent {

    void inject(SelectionDialogFragment fragment);

}
