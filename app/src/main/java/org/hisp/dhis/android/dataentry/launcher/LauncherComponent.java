package org.hisp.dhis.android.dataentry.launcher;

import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = LauncherModule.class)
public interface LauncherComponent {
    void inject(LauncherActivity launcherActivity);
}
