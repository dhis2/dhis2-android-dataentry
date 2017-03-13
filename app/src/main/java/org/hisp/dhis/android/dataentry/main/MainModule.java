package org.hisp.dhis.android.dataentry.main;

import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;
import org.hisp.dhis.android.dataentry.user.UserRepository;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
@PerActivity
public class MainModule {

    @Provides
    @PerActivity
    MainPresenter mainPresenter(SchedulerProvider schedulerProvider,
                                UserRepository userRepository) {
        return new MainPresenterImpl(schedulerProvider, userRepository);
    }
}
