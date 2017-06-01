package org.hisp.dhis.android.dataentry.dashboard;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = DashboardModule.class)
public interface DashboardComponent {
    void inject(DashboardFragment dashboardFragment);
}