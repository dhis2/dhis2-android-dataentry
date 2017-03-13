package org.hisp.dhis.android.dataentry.reports;

import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = ReportsModule.class)
public interface ReportsComponent {
    void inject(ReportsFragment reportsFragment);
}
