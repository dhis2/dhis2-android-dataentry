package org.hisp.dhis.android.dataentry.main;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.user.UserRepository;

import dagger.Module;
import dagger.Provides;

@Module
@PerActivity
public class MainModule {

    @Provides
    @PerActivity
    MainPresenter mainPresenter(D2 d2,
                                SchedulerProvider schedulerProvider,
                                UserRepository userRepository) {
        return new MainPresenterImpl(d2, schedulerProvider, userRepository);
    }
}
