package org.hisp.dhis.android.dataentry.reports.search;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.utils.StringUtils;
import org.hisp.dhis.android.dataentry.reports.ReportViewModel;

import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
final class SearchRepositoryImpl implements SearchRepository {
    private static final String SELECT_TEIS = "SELECT DISTINCT" +
            "  InstanceAttribute.tea," +
            "  InstanceAttribute.label," +
            "  TrackedEntityInstance.uid," +
            "  TrackedEntityInstance.state," +
            "  TrackedEntityAttributeValue.value " +
            "FROM (TrackedEntityInstance" +
            "  INNER JOIN Program ON Program.trackedEntity = TrackedEntityInstance.trackedEntity)" +
            "    LEFT OUTER JOIN (" +
            "      SELECT TrackedEntityAttribute.uid AS tea, " +
            "             TrackedEntityAttribute.displayName AS label, " +
            "             ProgramTrackedEntityAttribute.displayInList AS showInList, " +
            "             ProgramTrackedEntityAttribute.program AS program, " +
            "             ProgramTrackedEntityAttribute.sortOrder AS formOrder " +
            "      FROM ProgramTrackedEntityAttribute INNER JOIN TrackedEntityAttribute " +
            "        ON TrackedEntityAttribute.uid = ProgramTrackedEntityAttribute.trackedEntityAttribute" +
            "      ) AS InstanceAttribute ON InstanceAttribute.program = Program.uid " +
            "    AND InstanceAttribute.showInList = 1" +
            "  LEFT OUTER JOIN TrackedEntityAttributeValue" +
            "    ON (TrackedEntityAttributeValue.trackedEntityAttribute = InstanceAttribute.tea " +
            "    AND TrackedEntityAttributeValue.trackedEntityInstance = TrackedEntityInstance.uid) " +
            "WHERE TrackedEntityInstance.trackedEntity = '%s' AND NOT TrackedEntityInstance.state = 'TO_DELETE'" +
            "  AND TrackedEntityAttributeValue.value LIKE '%%%s%%' AND length('%s') != 0 " +
            "ORDER BY datetime(TrackedEntityInstance.created) DESC," +
            "  TrackedEntityInstance.uid ASC," +
            "  InstanceAttribute.formOrder ASC;";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final String trackedEntity;

    SearchRepositoryImpl(@NonNull BriteDatabase briteDatabase, @NonNull String trackedEntity) {
        this.briteDatabase = briteDatabase;
        this.trackedEntity = trackedEntity;
    }

    @NonNull
    @Override
    public Flowable<List<ReportViewModel>> search(@NonNull String token) {
        return toV2Flowable(briteDatabase
                .createQuery(TrackedEntityInstanceModel.TABLE, String.format(Locale.US, SELECT_TEIS,
                        trackedEntity, escapeSqlToken(token.trim()), escapeSqlToken(token.trim())))
                .mapToList(this::mapToPairs))
                .switchMap(rows -> Flowable.fromIterable(rows)
                        .groupBy(Pair::val0, Pair::val1)
                        .concatMap(group -> group.toList().toFlowable()
                                .map(values -> ReportViewModel.create(fromState(group.getKey().val1()),
                                        group.getKey().val0(), StringUtils.join(values))))
                        .toList().toFlowable());
    }

    @NonNull
    private static String escapeSqlToken(@NonNull String sqlString) {
        StringBuilder stringBuilder = new StringBuilder();
        if (sqlString.indexOf('\'') == -1) {
            return stringBuilder.append(sqlString).toString();
        }

        int length = sqlString.length();
        for (int i = 0; i < length; i++) {
            char c = sqlString.charAt(i);
            if (c == '\'') {
                stringBuilder.append('\'');
            }
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
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
        String trackedEntityAttribute = cursor.getString(1);
        String trackedEntityAttributeValue = cursor.getString(4);

        if (trackedEntityAttributeValue == null) {
            trackedEntityAttributeValue = "-";
        }

        String label = "";
        if (trackedEntityAttribute != null) {
            label = String.format(Locale.US, "%s: %s", trackedEntityAttribute, trackedEntityAttributeValue);
        }

        return Pair.create(Pair.create(cursor.getString(2), cursor.getString(3)), label);
    }
}
