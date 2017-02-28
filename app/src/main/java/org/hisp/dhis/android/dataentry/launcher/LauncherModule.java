package org.hisp.dhis.android.dataentry.launcher;

import android.support.annotation.Nullable;

import org.hisp.dhis.android.dataentry.commons.PerActivity;
import org.hisp.dhis.android.dataentry.server.UserManager;
import org.hisp.dhis.android.dataentry.server.ServerComponent;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
public final class LauncherModule {
    private final UserManager userManager;

    LauncherModule(@Nullable ServerComponent serverComponent) {
        this.userManager = serverComponent == null ?
                null : serverComponent.userManager();
    }

    @Provides
    @PerActivity
    LauncherPresenter launcherPresenter(SchedulerProvider schedulerProvider) {
        return new LauncherPresenterImpl(schedulerProvider, userManager);
    }
}
