package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.create.CreateItemsArgument.Type;

import dagger.Module;
import dagger.Provides;

@Module
@PerFragment
public final class CreateItemsModule {

    @NonNull
    private final CreateItemsArgument argument;

    @NonNull
    private final CreateItemsActivity activity;

    CreateItemsModule(@NonNull CreateItemsArgument argument, @NonNull CreateItemsActivity activity) {
        this.argument = argument;
        this.activity = activity;
    }

    @Provides
    @PerFragment
    CreateItemsRepository createItemsRepository(@NonNull BriteDatabase database,
            @NonNull CodeGenerator codeGenerator, @NonNull CurrentDateProvider currentDateProvider) {
        if (argument.type() == Type.TEI) {
            return new TeiRepositoryImpl(database, codeGenerator, currentDateProvider, argument.uid());
        } else if (argument.type() == Type.ENROLLMENT) {
            return new EnrollmentRepositoryImpl(database, codeGenerator,
                    currentDateProvider, argument.uid());
        } else if (argument.type() == Type.ENROLLMENT_EVENT) {
            return new EnrollmentEventRepositoryImpl(database, codeGenerator,
                    currentDateProvider, argument.enrollment()); //TODO: add argument as argument ? type?
        } else if (argument.type() == Type.EVENT) {
            return new SingleEventRepositoryImpl(database, codeGenerator, currentDateProvider);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + argument.type());
        }
    }

    @Provides
    @PerFragment
    CreateItemsPresenter createItemsPresenter(@NonNull CreateItemsRepository repository,
            @NonNull SchedulerProvider schedulerProvider) {
        return new CreateItemsPresenterImpl(argument, repository, schedulerProvider);
    }

    @Provides
    @PerFragment
    CreateItemsNavigator crateItemsNavigator() {
        if (argument.type() == Type.TEI) {
            return new TeiNavigator(activity);
        } else if (argument.type() == Type.ENROLLMENT) {
            return new EnrollmentNavigator(activity);
        } else if (argument.type() == Type.ENROLLMENT_EVENT) {
            return new EnrollmentEventNavigator(activity);
        } else if (argument.type() == Type.EVENT) {
            return new SingleEventsNavigator(activity);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + argument.type());
        }
    }
}
