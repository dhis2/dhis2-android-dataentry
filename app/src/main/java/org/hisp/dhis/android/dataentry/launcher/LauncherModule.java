package org.hisp.dhis.android.dataentry.launcher;

import android.support.annotation.Nullable;

import org.hisp.dhis.android.dataentry.commons.PerActivity;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.server.ServerComponent;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
public final class LauncherModule {
    private final ConfigurationRepository configurationRepository;

    LauncherModule(@Nullable ServerComponent serverComponent) {
        this.configurationRepository = serverComponent == null ?
                null : serverComponent.configurationRepository();
    }

    @Provides
    @PerActivity
    LauncherPresenter providesLauncherPresenter(SchedulerProvider schedulerProvider) {
        return new LauncherPresenterImpl(schedulerProvider, configurationRepository);
    }
}
