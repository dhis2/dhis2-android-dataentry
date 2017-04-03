package org.hisp.dhis.android.dataentry.reports;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
final class SingleEventsRepositoryImpl implements ReportsRepository {
    private static final String SELECT_EVENTS = "SELECT" +
            "  Event.uid," +
            "  Event.state," +
            "  DataElement.displayName," +
            "  TrackedEntityDataValue.value " +
            "FROM Event" +
            "  LEFT OUTER JOIN (" +
            "      ProgramStageDataElement INNER JOIN DataElement " +
            "      ON DataElement.uid = ProgramStageDataElement.dataElement" +
            "    ) ON (ProgramStageDataElement.programStage = Event.programStage " +
            "               AND ProgramStageDataElement.displayInReports = 1)" +
            "  LEFT OUTER JOIN TrackedEntityDataValue" +
            "    ON (TrackedEntityDataValue.event = Event.uid " +
            "               AND TrackedEntityDataValue.dataElement = DataElement.uid)" +
            "WHERE Event.program = ? AND NOT Event.state = 'TO_DELETE'" +
            "ORDER BY datetime(Event.created) DESC," +
            "  ProgramStageDataElement.sortOrder ASC," +
            "  Event.uid ASC;";

    @NonNull
    private final String programUid;

    @NonNull
    private final BriteDatabase briteDatabase;

    SingleEventsRepositoryImpl(@NonNull BriteDatabase briteDatabase, @NonNull String programUid) {
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
        String dataElement = cursor.getString(2);
        String dataValue = cursor.getString(3);

        if (dataValue == null) {
            dataValue = "-";
        }

        String label = "";
        if (dataElement != null) {
            label = String.format(Locale.US, "%s: %s", dataElement, dataValue);
        }

        return Pair.create(Pair.create(cursor.getString(0), cursor.getString(1)), label);
    }
}
