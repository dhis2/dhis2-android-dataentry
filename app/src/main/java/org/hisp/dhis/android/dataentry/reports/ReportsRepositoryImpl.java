package org.hisp.dhis.android.dataentry.reports;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

final class ReportsRepositoryImpl implements ReportsRepository {
    /*
        SELECT
            Event.uid,
            Event.state,
            DataElement.displayName,
            TrackedEntityDataValue.value
        FROM TrackedEntityDataValue
            LEFT JOIN Event ON TrackedEntityDataValue.event = Event.uid
            LEFT JOIN DataElement ON TrackedEntityDataValue.dataElement = DataElement.uid
            LEFT JOIN ProgramStageDataElement ON TrackedEntityDataValue.dataElement = ProgramStageDataElement.dataElement
        WHERE Event.program = "program_uid" AND NOT Event.state = 'TO_DELETE' AND ProgramStageDataElement.displayInReports = 1
        ORDER BY datetime(Event.created) DESC, Event.uid ASC, ProgramStageDataElement.sortOrder ASC;
     */
    private static final String SELECT_EVENTS = "SELECT " +
            EventModel.TABLE + "." + EventModel.Columns.UID + ", " +
            EventModel.TABLE + "." + EventModel.Columns.STATE + ", " +
            DataElementModel.TABLE + "." + DataElementModel.Columns.DISPLAY_NAME + ", " +
            TrackedEntityDataValueModel.TABLE + "." + TrackedEntityDataValueModel.Columns.VALUE + " " +
            "FROM " + TrackedEntityDataValueModel.TABLE + " " +
            "LEFT JOIN " + EventModel.TABLE + " ON " +
            TrackedEntityDataValueModel.TABLE + "." + TrackedEntityDataValueModel.Columns.EVENT + " = " +
            EventModel.TABLE + "." + EventModel.Columns.UID + " " +
            "LEFT JOIN " + DataElementModel.TABLE + " ON " + TrackedEntityDataValueModel.TABLE + "." +
            TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = " +
            DataElementModel.TABLE + "." + DataElementModel.Columns.UID + " " +
            "LEFT JOIN " + ProgramStageDataElementModel.TABLE + " ON " +
            TrackedEntityDataValueModel.TABLE + "." + TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = " +
            ProgramStageDataElementModel.TABLE + "." + ProgramStageDataElementModel.Columns.DATA_ELEMENT + " " +
            "WHERE " + EventModel.TABLE + "." + EventModel.Columns.PROGRAM + " = ?" + "AND NOT " +
            EventModel.TABLE + "." + EventModel.Columns.STATE + " = " + '"' + State.TO_DELETE.toString() + '"' + " AND " +
            ProgramStageDataElementModel.TABLE + "." + ProgramStageDataElementModel.Columns.DISPLAY_IN_REPORTS + " = 1 " +
            "ORDER BY datetime(" + EventModel.TABLE + "." + EventModel.Columns.CREATED + ") DESC" + "," +
            EventModel.TABLE + "." + EventModel.Columns.UID + " ASC," +
            ProgramStageDataElementModel.TABLE + "." + ProgramStageDataElementModel.Columns.SORT_ORDER + " ASC;";

    @NonNull
    private final String programUid;

    @NonNull
    private final BriteDatabase briteDatabase;

    ReportsRepositoryImpl(@NonNull BriteDatabase briteDatabase, @NonNull String programUid) {
        this.briteDatabase = briteDatabase;
        this.programUid = programUid;
    }

    @NonNull
    @Override
    public Flowable<List<ReportViewModel>> reports() {
        return toV2Flowable(briteDatabase.createQuery(EventModel.TABLE, SELECT_EVENTS, programUid)
                .mapToList(this::mapToPairs))
                .switchMap(rows -> Flowable.fromIterable(rows)
                        .groupBy(Pair::val0, Pair::val1)
                        .concatMap(group -> group.toList().toFlowable()
                                .map(values -> ReportViewModel.create(group.getKey().val0(),
                                        fromState(group.getKey().val1()), values)))
                        .toList().toFlowable());
    }

    private ReportViewModel.Status fromState(String state) {
        switch (State.valueOf(state)) {
            case TO_POST:
            case TO_UPDATE:
                return ReportViewModel.Status.TO_SYNC;
            case ERROR:
                return ReportViewModel.Status.FAILED;
            case SYNCED:
                return ReportViewModel.Status.SYNCED;
            default:
                throw new IllegalArgumentException("Unsupported state: " + state);
        }
    }

    private Pair<Pair<String, String>, String> mapToPairs(@NonNull Cursor cursor) {
        return Pair.create(Pair.create(cursor.getString(0), cursor.getString(1)),
                String.format(Locale.US, "%s: %s", cursor.getString(2), cursor.getString(3)));
    }
}
