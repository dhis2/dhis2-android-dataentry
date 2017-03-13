package org.hisp.dhis.android.dataentry.main.home;

import org.hisp.dhis.android.dataentry.commons.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = HomeModule.class)
public interface HomeComponent {
    void inject(HomeFragment homeFragment);
}
