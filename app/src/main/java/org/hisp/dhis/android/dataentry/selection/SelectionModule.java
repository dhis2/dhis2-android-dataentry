package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@PerFragment
@Module
public final class SelectionModule {

    @NonNull
    private final SelectionArgument argument;

    SelectionModule(@NonNull SelectionArgument argument) {
        this.argument = argument;
    }

    @PerFragment
    @Provides
    SelectionRepository selectionRepository(BriteDatabase database) {
        return new OptionSetRepositoryImpl(database);
    }

    @PerFragment
    @Provides
    SelectionPresenter selectionPresenter(SelectionRepository repository, SchedulerProvider schedulerProvider) {
        return new SelectionPresenterImpl(argument, repository, schedulerProvider);
    }
}
