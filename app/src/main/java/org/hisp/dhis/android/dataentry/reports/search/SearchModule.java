package org.hisp.dhis.android.dataentry.reports.search;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.reports.ReportsNavigator;

import dagger.Module;
import dagger.Provides;

@Module
@PerActivity
public final class SearchModule {

    @NonNull
    private final Activity activity;

    @NonNull
    private final SearchArguments searchArguments;

    public SearchModule(@NonNull Activity activity, @NonNull SearchArguments searchArguments) {
        this.activity = activity;
        this.searchArguments = searchArguments;
    }

    @PerActivity
    @Provides
    ReportsNavigator reportsNavigator() {
        return new SearchNavigator(activity, searchArguments.entityName());
    }

    @PerActivity
    @Provides
    SearchRepository searchRepository(@NonNull BriteDatabase briteDatabase) {
        return new SearchRepositoryImpl(briteDatabase, searchArguments.entityUid());
    }


    @PerActivity
    @Provides
    SearchPresenter searchPresenter(SearchRepository searchRepository,
            SchedulerProvider schedulerProvider) {
        return new SearchPresenterImpl(searchArguments, schedulerProvider, searchRepository);
    }
}
