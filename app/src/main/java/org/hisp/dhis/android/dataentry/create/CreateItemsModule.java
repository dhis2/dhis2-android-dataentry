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

@PerFragment
@Module
public class CreateItemsModule {

    @NonNull
    private final CreateItemsArgument argument;

    @NonNull
    private final CreateItemsActivity activity;

    CreateItemsModule(@NonNull CreateItemsArgument argument, @NonNull CreateItemsActivity activity) {
        this.argument = argument;
        this.activity = activity;
    }

    @PerFragment
    @Provides
    CreateItemsRepository createItemsRepository(@NonNull BriteDatabase database,
                                                @NonNull CodeGenerator codeGenerator,
                                                @NonNull CurrentDateProvider currentDateProvider) {
        if (argument.type() == Type.TEI) {
            return new TeiRepositoryImpl(database, codeGenerator, currentDateProvider);
        } else if (argument.type() == Type.ENROLLMENT) {
            return new EnrollmentRepositoryImpl(database, codeGenerator,
                    currentDateProvider, argument.uid());
        } else if (argument.type() == Type.ENROLMENT_EVENT) {
            return new EnrollmentEventRepositoryImpl(database, codeGenerator,
                    currentDateProvider, argument.uid()); //TODO: add argument as argument ? type?
        } else if (argument.type() == Type.EVENT) {
            return new SingleEventRepositoryImpl(database, codeGenerator, currentDateProvider);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + argument.type());
        }
    }

    @PerFragment
    @Provides
    CreateItemsPresenter createItemsPresenter(CreateItemsRepository repository, SchedulerProvider schedulerProvider) {
        return new CreateItemsPresenterImpl(argument, repository, schedulerProvider);
    }

    @PerFragment
    @Provides
    CreateItemsNavigator crateItemsNavigator() {
        if (argument.type() == Type.TEI) {
            return new TeiNavigator(activity, "");
        } else if (argument.type() == Type.ENROLLMENT) {
            return new EnrollmentsNavigator(activity);
        } else if (argument.type() == Type.ENROLMENT_EVENT) {
            return new EnrollmentsNavigator(activity);
        } else if (argument.type() == Type.EVENT) {
            return new SingleEventsNavigator(activity);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + argument.type());
        }
    }
}
