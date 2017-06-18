package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
@PerFragment
public final class SelectionModule {

    @NonNull
    private final SelectionArgument argument;

    @NonNull
    private final SelectionDialogFragment selectionDialogFragment;

    SelectionModule(@NonNull SelectionArgument argument,
            @NonNull SelectionDialogFragment dialogFragment) {
        this.argument = argument;
        this.selectionDialogFragment = dialogFragment;
    }

    @Provides
    @PerFragment
    SelectionHandler selectionHandler() {
        return new NoopSelectionHandler();
    }

    @Provides
    @PerFragment
    SelectionNavigator selectionNavigator() {
        if (argument.type() == SelectionArgument.Type.ORGANISATION ||
                argument.type() == SelectionArgument.Type.PROGRAM ||
                argument.type() == SelectionArgument.Type.PROGRAM_STAGE) {
            return new OnActivityResultNavigator(selectionDialogFragment);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + argument.type());
        }
    }

    @Provides
    @PerFragment
    SelectionRepository selectionRepository(@NonNull BriteDatabase database) {
        if (argument.type() == SelectionArgument.Type.ORGANISATION) {
            return new OrganisationUnitRepositoryImpl(database);
        } else if (argument.type() == SelectionArgument.Type.PROGRAM) {
            return new ProgramRepositoryImpl(database, argument.uid());
        } else if (argument.type() == SelectionArgument.Type.PROGRAM_STAGE) {
            return new ProgramStageRepositoryImpl(database, argument.uid());
        } else {
            throw new IllegalArgumentException("Unsupported type: " + argument.type());
        }
    }

    @Provides
    @PerFragment
    SelectionPresenter selectionPresenter(@NonNull SelectionRepository repository,
            @NonNull SelectionHandler selectionHandler, @NonNull SchedulerProvider schedulerProvider) {
        return new SelectionPresenterImpl(argument.name(), repository,
                selectionHandler, schedulerProvider);
    }
}
