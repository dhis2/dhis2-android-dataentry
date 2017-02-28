package org.hisp.dhis.android.dataentry.login;

import org.hisp.dhis.android.dataentry.commons.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = LoginModule.class)
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
