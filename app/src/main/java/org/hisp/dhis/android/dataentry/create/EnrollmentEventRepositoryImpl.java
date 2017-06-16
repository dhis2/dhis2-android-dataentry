package org.hisp.dhis.android.dataentry.create;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.dataentry.commons.tuples.Single;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;

import java.util.Locale;

import io.reactivex.Observable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Observable;

final class EnrollmentEventRepositoryImpl implements CreateItemsRepository {
    private static final String SELECT_PROGRAM = "SELECT program FROM ProgramStage " +
            "WHERE uid = ? LIMIT 1;";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final CodeGenerator codeGenerator;

    @NonNull
    private final CurrentDateProvider currentDateProvider;

    @NonNull
    private final String enrollment;

    EnrollmentEventRepositoryImpl(@NonNull BriteDatabase briteDatabase, @NonNull CodeGenerator codeGenerator,
            @NonNull CurrentDateProvider currentDateProvider, @NonNull String enrollment) {
        this.briteDatabase = briteDatabase;
        this.codeGenerator = codeGenerator;
        this.currentDateProvider = currentDateProvider;
        this.enrollment = enrollment;
    }

    @NonNull
    @Override
    public Observable<String> save(@NonNull String orgUnit, @NonNull String programStage) {
        return toV2Observable(briteDatabase.createQuery(ProgramStageModel.TABLE, SELECT_PROGRAM, programStage)
                .mapToOne(cursor -> Single.create(cursor.getString(0))).take(1))
                .map(program -> EventModel.builder()
                        .uid(codeGenerator.generate())
                        .created(currentDateProvider.currentDate())
                        .lastUpdated(currentDateProvider.currentDate())
                        .eventDate(currentDateProvider.currentDate())
                        .enrollmentUid(enrollment)
                        .program(program.val0())
                        .programStage(programStage)
                        .organisationUnit(orgUnit)
                        .status(EventStatus.ACTIVE)
                        .state(State.TO_POST)
                        .build())
                .switchMap(event -> {
                    if (briteDatabase.insert(EventModel.TABLE, event.toContentValues()) < 0) {
                        String errorMessage = String.format(Locale.US, "Failed to insert new event for " +
                                "organisationUnit=[%s] and program=[%s]", orgUnit, programStage);
                        return Observable.error(new SQLiteConstraintException(errorMessage));
                    }
                    return Observable.just(event.uid());
                });
    }
}
