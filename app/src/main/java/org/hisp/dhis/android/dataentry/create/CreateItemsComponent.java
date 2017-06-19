package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = CreateItemsModule.class)
public interface CreateItemsComponent {
    void inject(@NonNull CreateItemsFragment fragment);
}