package org.hisp.dhis.android.dataentry.create;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;

import java.util.Locale;

import io.reactivex.Observable;

class EnrollmentRepositoryImpl implements CreateItemsRepository {

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final CodeGenerator codeGenerator;

    @NonNull
    private final CurrentDateProvider currentDateProvider;

    @NonNull
    private final String trackedEntityInstance;

    EnrollmentRepositoryImpl(@NonNull BriteDatabase briteDatabase,
            @NonNull CodeGenerator codeGenerator,
            @NonNull CurrentDateProvider currentDateProvider,
            @NonNull String trackedEntityInstance) {
        this.briteDatabase = briteDatabase;
        this.codeGenerator = codeGenerator;
        this.currentDateProvider = currentDateProvider;
        this.trackedEntityInstance = trackedEntityInstance;
    }

    @NonNull
    @Override
    public Observable<String> save(@NonNull String orgUnit, @NonNull String program) {
        return Observable.defer(() -> {
            EnrollmentModel enrollmentModel = EnrollmentModel.builder()
                    .uid(codeGenerator.generate())
                    .created(currentDateProvider.currentDate())
                    .lastUpdated(currentDateProvider.currentDate())
                    .dateOfEnrollment(currentDateProvider.currentDate())
                    .program(program)
                    .organisationUnit(orgUnit)
                    .trackedEntityInstance(trackedEntityInstance)
                    .enrollmentStatus(EnrollmentStatus.ACTIVE)
                    .state(State.TO_POST)
                    .build();

            if (briteDatabase.insert(EnrollmentModel.TABLE,
                    enrollmentModel.toContentValues()) < 0) {
                String errorMessage = String.format(Locale.US, "Failed to insert new event for " +
                        "organisationUnit=[%s] and program=[%s]", orgUnit, program);
                return Observable.error(new SQLiteConstraintException(errorMessage));
            }

            return Observable.just(enrollmentModel.uid());
        });
    }
}
