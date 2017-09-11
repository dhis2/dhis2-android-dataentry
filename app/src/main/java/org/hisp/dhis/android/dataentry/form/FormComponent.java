package org.hisp.dhis.android.dataentry.form;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryComponent;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryModule;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryStoreModule;

import dagger.Subcomponent;

@PerForm
@Subcomponent(modules = FormModule.class)
public interface FormComponent {

    @NonNull
    DataEntryComponent plus(@NonNull DataEntryModule dataEntryModule,
            @NonNull DataEntryStoreModule dataEntryStoreModule);

    void inject(FormFragment formFragment);
}