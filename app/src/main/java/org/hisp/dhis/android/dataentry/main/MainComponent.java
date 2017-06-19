package org.hisp.dhis.android.dataentry.main;

import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = MainModule.class)
public interface MainComponent {
    void inject(MainActivity mainActivity);
}
