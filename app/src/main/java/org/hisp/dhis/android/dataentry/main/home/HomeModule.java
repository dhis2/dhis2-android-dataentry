package org.hisp.dhis.android.dataentry.main.home;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.utils.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
@PerFragment
public class HomeModule {

    @Provides
    @PerFragment
    HomePresenter homePresenter(SchedulerProvider schedulerProvider,
                                HomeRepository homeRepository) {
        return new HomePresenterImpl(schedulerProvider, homeRepository);
    }

    @Provides
    @PerFragment
    HomeRepository homeRepository(BriteDatabase briteDatabase) {
        return new HomeRepositoryImpl(briteDatabase);
    }
}
