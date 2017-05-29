package org.hisp.dhis.android.dataentry.reports.search;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerActivity;
import org.hisp.dhis.android.dataentry.reports.ReportsArguments;
import org.hisp.dhis.android.dataentry.reports.ReportsNavigator;

import dagger.Module;
import dagger.Provides;

@Module
@PerActivity
public final class SearchModule {

    @NonNull
    private final Activity activity;

    @NonNull
    private final ReportsArguments reportsArguments;

    public SearchModule(@NonNull Activity activity, @NonNull ReportsArguments reportsArguments) {
        this.activity = activity;
        this.reportsArguments = reportsArguments;
    }

    @PerActivity
    @Provides
    ReportsNavigator reportsNavigator() {
        return new SearchNavigator(activity, reportsArguments.entityName());
    }

    @PerActivity
    @Provides
    SearchRepository searchRepository(@NonNull BriteDatabase briteDatabase) {
        return new SearchRepositoryImpl(briteDatabase, reportsArguments.entityUid());
    }
}
