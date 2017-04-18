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
            "  EventDataElement.label," +
            "  TrackedEntityDataValue.value " +
            "FROM Event" +
            "  LEFT OUTER JOIN (" +
            "    SELECT  DataElement.uid AS de," +
            "           DataElement.displayName AS label," +
            "           ProgramStageDataElement.displayInReports AS showInReports, " +
            "           ProgramStageDataElement.sortOrder AS formOrder, " +
            "           ProgramStageDataElement.programStage AS stage " +
            "       FROM ProgramStageDataElement INNER JOIN DataElement " +
            "           ON DataElement.uid = ProgramStageDataElement.dataElement" +
            "    ) AS EventDataElement ON (EventDataElement.stage = Event.programStage " +
            "      AND EventDataElement.showInReports = 1)" +
            "  LEFT OUTER JOIN TrackedEntityDataValue" +
            "    ON (TrackedEntityDataValue.event = Event.uid " +
            "    AND TrackedEntityDataValue.dataElement = EventDataElement.de) " +
            "WHERE Event.program = ? AND NOT Event.state = 'TO_DELETE' " +
            "ORDER BY datetime(Event.created) DESC," +
            "  EventDataElement.formOrder ASC," +
            "  Event.uid ASC;";

    @NonNull
    private final BriteDatabase briteDatabase;

    SingleEventsRepositoryImpl(@NonNull BriteDatabase briteDatabase) {
        this.briteDatabase = briteDatabase;
    }

    @NonNull
    @Override
    public Flowable<List<ReportViewModel>> reports(@NonNull String uid) {
        return toV2Flowable(briteDatabase.createQuery(EventModel.TABLE, SELECT_EVENTS, uid)
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
