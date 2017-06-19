package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryStore;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryStoreModule;

import dagger.Module;
import dagger.Provides;

@PerFragment
@Module(includes = DataEntryStoreModule.class)
public final class OptionSelectionModule {

    @NonNull
    private final OptionSelectionArgument argument;

    @NonNull
    private final SelectionDialogFragment selectionDialogFragment;

    OptionSelectionModule(@NonNull OptionSelectionArgument argument,
            @NonNull SelectionDialogFragment dialogFragment) {
        this.argument = argument;
        this.selectionDialogFragment = dialogFragment;
    }

    @Provides
    @PerFragment
    SelectionHandler selectionHandler(@NonNull DataEntryStore dataEntryStore) {
        return new OptionPersistenceHandler(dataEntryStore, argument.field());
    }

    @Provides
    @PerFragment
    SelectionNavigator selectionNavigator() {
        return new DismissNavigator(selectionDialogFragment);
    }

    @Provides
    @PerFragment
    SelectionRepository selectionRepository(@NonNull BriteDatabase database) {
        return new OptionSetRepositoryImpl(database, argument.optionSet());
    }

    @Provides
    @PerFragment
    SelectionPresenter selectionPresenter(@NonNull SelectionHandler selectionHandler,
            @NonNull SelectionRepository repository, @NonNull SchedulerProvider schedulerProvider) {
        return new SelectionPresenterImpl(argument.fieldName(),
                repository, selectionHandler, schedulerProvider);
    }
}
