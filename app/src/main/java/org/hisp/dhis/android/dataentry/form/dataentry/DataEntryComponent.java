package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = DataEntryModule.class)
public interface DataEntryComponent {
    void inject(@NonNull DataEntryFragment dataEntryFragment);
}
