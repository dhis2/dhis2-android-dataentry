package org.hisp.dhis.android.dataentry.create;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.commons.dagger.PerFragment;
import org.hisp.dhis.android.dataentry.commons.schedulers.SchedulerProvider;
import org.hisp.dhis.android.dataentry.create.CreateItemsArgument.Type;

import dagger.Module;
import dagger.Provides;

@PerFragment
@Module
public class CreateItemsModule {

    @NonNull
    private final CreateItemsArgument argument;

    CreateItemsModule(@NonNull CreateItemsArgument argument) {
        this.argument = argument;
    }

    @PerFragment
    @Provides
    CreateItemsRepository creationRepository(BriteDatabase database) {
        if (argument.type() == Type.TEI) {
            return new TeiCreationRepositoryImpl(database);
        } else if (argument.type() == Type.ENROLLMENT) {
            return new EnrollmentRepositoryImpl(database);
        } else if (argument.type() == Type.ENROLMENT_EVENT) {
            return new EnrollmentRepositoryImpl(database);
        } else if (argument.type() == Type.EVENT) {
            return new EventRepositoryImpl(database);
        } else {
            throw new IllegalStateException("CreateItems Type is unknown.");
        }
    }

    @PerFragment
    @Provides
    CreateItemsPresenter creationPresenter(CreateItemsRepository repository, SchedulerProvider schedulerProvider) {
        return new CreateItemsPresenterImpl(argument, repository, schedulerProvider);
    }
}
