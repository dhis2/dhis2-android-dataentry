package org.hisp.dhis.android.dataentry.reports;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
final class TeisRepositoryImpl implements ReportsRepository {
    private static final String SELECT_TEIS = "SELECT" +
            "  TrackedEntityInstance.uid," +
            "  TrackedEntityInstance.state," +
            "  TrackedEntityAttribute.displayName," +
            "  TrackedEntityAttributeValue.value " +
            "FROM (TrackedEntityInstance" +
            "  INNER JOIN Program ON Program.trackedEntity = TrackedEntityInstance.trackedEntity)" +
            "  LEFT OUTER JOIN (" +
            "      ProgramTrackedEntityAttribute INNER JOIN TrackedEntityAttribute " +
            "        ON TrackedEntityAttribute.uid = ProgramTrackedEntityAttribute.trackedEntityAttribute" +
            "    ) ON ProgramTrackedEntityAttribute.program = Program.uid " +
            "        AND ProgramTrackedEntityAttribute.displayInList = 1" +
            "  LEFT OUTER JOIN TrackedEntityAttributeValue" +
            "    ON (TrackedEntityAttributeValue.trackedEntityAttribute = TrackedEntityAttribute.uid AND" +
            "        TrackedEntityAttributeValue.trackedEntityInstance = TrackedEntityInstance.uid)" +
            "WHERE TrackedEntityInstance.trackedEntity = ? AND NOT TrackedEntityInstance.state = 'TO_DELETE'" +
            "ORDER BY datetime(TrackedEntityInstance.created) DESC," +
            "  TrackedEntityInstance.uid ASC," +
            "  ProgramTrackedEntityAttribute.sortOrder ASC;";

    @NonNull
    private final String trackedEntityUid;

    @NonNull
    private final BriteDatabase briteDatabase;

    TeisRepositoryImpl(@NonNull BriteDatabase briteDatabase, @NonNull String trackedEntityUid) {
        this.briteDatabase = briteDatabase;
        this.trackedEntityUid = trackedEntityUid;
    }

    @NonNull
    @Override
    public Flowable<List<ReportViewModel>> reports() {
        return toV2Flowable(briteDatabase.createQuery(TrackedEntityInstanceModel.TABLE, SELECT_TEIS, trackedEntityUid)
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
        String trackedEntityAttribute = cursor.getString(2);
        String trackedEntityAttributeValue = cursor.getString(3);

        if (trackedEntityAttributeValue == null) {
            trackedEntityAttributeValue = "-";
        }

        String label = "";
        if (trackedEntityAttribute != null) {
            label = String.format(Locale.US, "%s: %s", trackedEntityAttribute, trackedEntityAttributeValue);
        }

        return Pair.create(Pair.create(cursor.getString(0), cursor.getString(1)), label);
    }
}
