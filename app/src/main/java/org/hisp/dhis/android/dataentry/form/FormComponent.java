package org.hisp.dhis.android.dataentry.form;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = FormModule.class)
public interface FormComponent {
    void inject(FormFragment formFragment);
}