package org.hisp.dhis.android.dataentry.create;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = CreateItemsModule.class)
public interface CreateItemsComponent {

    void inject(CreateItemsFragment fragment);

}