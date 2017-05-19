package org.hisp.dhis.android.dataentry.dashboard;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.form.FormFragment;
import org.hisp.dhis.android.dataentry.form.FormModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = DashboardModule.class)
public interface DashboardComponent {
    void inject(DashboardFragment dashboardFragment);
}