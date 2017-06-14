package org.hisp.dhis.android.dataentry.create;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
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


    TeiRepositoryImpl(@NonNull BriteDatabase briteDatabase, @NonNull CodeGenerator codeGenerator,
            @NonNull CurrentDateProvider currentDateProvider) {
        this.briteDatabase = briteDatabase;
        this.codeGenerator = codeGenerator;
        this.currentDateProvider = currentDateProvider;
    }

    @NonNull
    @Override
    public Observable<String> save(@NonNull String orgUnit, @NonNull String trackedEntity) {
        return Observable.defer(() -> {
            TrackedEntityInstanceModel trackedEntityInstanceModel =
                    TrackedEntityInstanceModel.builder()
                            .uid(codeGenerator.generate())
                            .created(currentDateProvider.currentDate())
                            .lastUpdated(currentDateProvider.currentDate())
                            .organisationUnit(orgUnit)
                            .trackedEntity(trackedEntity)
                            .state(State.TO_POST)
                            .build();
            if (briteDatabase.insert(TrackedEntityInstanceModel.TABLE,
                    trackedEntityInstanceModel.toContentValues()) < 0) {
                String message = String.format(Locale.US, "Failed to insert new tracked entity " +
                        "instance for organisationUnit=[%s] and trackedEntity=[%s]", orgUnit, trackedEntity);
                return Observable.error(new SQLiteConstraintException(message));
            }

            return Observable.just(trackedEntityInstanceModel.uid());
        });
    }
}
