package org.hisp.dhis.android.dataentry.reports.search;

import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = SearchModule.class)
public interface SearchComponent {
    void inject(SearchFragment searchFragment);
}
