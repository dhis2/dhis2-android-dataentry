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

final class SingleEventRepositoryImpl implements CreateItemsRepository {
    private static final String SELECT_PROGRAM_STAGE = "SELECT uid FROM ProgramStage " +
            "WHERE program = ? LIMIT 1;";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final CodeGenerator codeGenerator;

    @NonNull
    private final CurrentDateProvider currentDateProvider;

    SingleEventRepositoryImpl(@NonNull BriteDatabase briteDatabase, @NonNull CodeGenerator codeGenerator,
            @NonNull CurrentDateProvider currentDateProvider) {
        this.briteDatabase = briteDatabase;
        this.codeGenerator = codeGenerator;
        this.currentDateProvider = currentDateProvider;
    }

    @NonNull
    @Override
    public Observable<String> save(@NonNull String organisationUnit, @NonNull String program) {
        return toV2Observable(briteDatabase.createQuery(ProgramStageModel.TABLE, SELECT_PROGRAM_STAGE, program)
                .mapToOne(cursor -> Single.create(cursor.getString(0))).take(1))
                .map(stage -> EventModel.builder()
                        .uid(codeGenerator.generate())
                        .created(currentDateProvider.currentDate())
                        .lastUpdated(currentDateProvider.currentDate())
                        .eventDate(currentDateProvider.currentDate())
                        .program(program)
                        .programStage(stage.val0())
                        .organisationUnit(organisationUnit)
                        .status(EventStatus.ACTIVE)
                        .state(State.TO_POST)
                        .build())
                .switchMap(event -> {
                    if (briteDatabase.insert(EventModel.TABLE, event.toContentValues()) < 0) {
                        String errorMessage = String.format(Locale.US, "Failed to insert new event for " +
                                "organisationUnit=[%s] and program=[%s]", organisationUnit, program);
                        return Observable.error(new SQLiteConstraintException(errorMessage));
                    }
                    return Observable.just(event.uid());
                });
    }
}
