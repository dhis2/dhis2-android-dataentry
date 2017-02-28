package org.hisp.dhis.android.dataentry.home;

import org.hisp.dhis.android.dataentry.commons.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = HomeModule.class)
public interface HomeComponent {
    void inject(HomeActivity homeActivity);
}
