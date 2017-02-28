package org.hisp.dhis.android.dataentry.home;

import org.hisp.dhis.android.dataentry.commons.PerActivity;
import org.hisp.dhis.android.dataentry.user.UserRepository;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
@PerActivity
public class HomeModule {

    @Provides
    @PerActivity
    HomePresenter homePresenter(SchedulerProvider schedulerProvider,
            UserRepository userRepository) {
        return new HomePresenterImpl(schedulerProvider, userRepository);
    }
}
