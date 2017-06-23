package org.hisp.dhis.android.dataentry.dashboard.navigation;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = NavigationModule.class)
public interface NavigationComponent {
    void inject(NavigationFragment navigationFragment);
}