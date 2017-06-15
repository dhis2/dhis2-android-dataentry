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
        if (argument.type().equals(SelectionArgument.Type.ORGANISATION)) {
            return new OrganisationUnitRepositoryImpl(database);
        } else if (argument.type().equals(SelectionArgument.Type.PROGRAM)) {
            return new ProgramRepositoryImpl(database, argument.uid());
        } else if (argument.type() == SelectionArgument.Type.OPTION) {
            return new OptionSetRepositoryImpl(database, argument.uid());
        } else {
            throw new IllegalStateException("Type does not correspond to a Repository implementation.");
        }
    }

    @PerFragment
    @Provides
    SelectionPresenter selectionPresenter(SelectionRepository repository, SchedulerProvider schedulerProvider) {
        return new SelectionPresenterImpl(argument, repository, schedulerProvider);
    }
}
