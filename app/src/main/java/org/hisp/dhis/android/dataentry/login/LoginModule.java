package org.hisp.dhis.android.dataentry.login;

import org.hisp.dhis.android.dataentry.Components;
import org.hisp.dhis.android.dataentry.commons.PerActivity;
import org.hisp.dhis.android.dataentry.server.ConfigurationRepository;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
@PerActivity
public class LoginModule {

    @Provides
    @PerActivity
    LoginPresenter loginPresenter(Components components, SchedulerProvider schedulerProvider,
            ConfigurationRepository configurationRepository) {
        return new LoginPresenterImpl(components, schedulerProvider, configurationRepository);
    }
}
