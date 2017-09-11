package org.hisp.dhis.android.dataentry.create;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;

import java.util.Locale;

import io.reactivex.Observable;


class TeiRepositoryImpl implements CreateItemsRepository {

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final CodeGenerator codeGenerator;

    @NonNull
    private final CurrentDateProvider currentDateProvider;

    @NonNull
    private final String trackedEntityUid;

    TeiRepositoryImpl(@NonNull BriteDatabase briteDatabase, @NonNull CodeGenerator codeGenerator,
            @NonNull CurrentDateProvider currentDateProvider, @NonNull String trackedEntityUid) {
        this.briteDatabase = briteDatabase;
        this.codeGenerator = codeGenerator;
        this.currentDateProvider = currentDateProvider;
        this.trackedEntityUid = trackedEntityUid;
    }

    @NonNull
    @Override
    public Observable<String> save(@NonNull String orgUnit, @NonNull String programUid) {
        return Observable.defer(() -> {
            TrackedEntityInstanceModel trackedEntityInstanceModel =
                    TrackedEntityInstanceModel.builder()
                            .uid(codeGenerator.generate())
                            .created(currentDateProvider.currentDate())
                            .lastUpdated(currentDateProvider.currentDate())
                            .organisationUnit(orgUnit)
                            .trackedEntity(trackedEntityUid)
                            .state(State.TO_POST)
                            .build();
            if (briteDatabase.insert(TrackedEntityInstanceModel.TABLE,
                    trackedEntityInstanceModel.toContentValues()) < 0) {
                String message = String.format(Locale.US, "Failed to insert new tracked entity " +
                                "instance for organisationUnit=[%s] and trackedEntity=[%s]",
                        orgUnit, trackedEntityUid);
                return Observable.error(new SQLiteConstraintException(message));
            }

            EnrollmentModel enrollmentModel = EnrollmentModel.builder()
                    .uid(codeGenerator.generate())
                    .created(currentDateProvider.currentDate())
                    .lastUpdated(currentDateProvider.currentDate())
                    .dateOfEnrollment(currentDateProvider.currentDate())
                    .program(programUid)
                    .organisationUnit(orgUnit)
                    .trackedEntityInstance(trackedEntityInstanceModel.uid())
                    .enrollmentStatus(EnrollmentStatus.ACTIVE)
                    .state(State.TO_POST)
                    .build();

            if (briteDatabase.insert(EnrollmentModel.TABLE, enrollmentModel.toContentValues()) < 0) {
                String message = String.format(Locale.US, "Failed to insert new enrollment " +
                        "instance for organisationUnit=[%s] and program=[%s]", orgUnit, programUid);
                return Observable.error(new SQLiteConstraintException(message));
            }

            return Observable.just(enrollmentModel.uid());
        });
    }
}
