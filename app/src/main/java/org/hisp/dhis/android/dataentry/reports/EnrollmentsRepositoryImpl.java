package org.hisp.dhis.android.dataentry.reports;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.commons.tuples.Sextet;
import org.hisp.dhis.android.dataentry.commons.utils.StringUtils;

import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;
import static org.hisp.dhis.android.dataentry.commons.utils.DbUtils.string;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
final class EnrollmentsRepositoryImpl implements ReportsRepository {
    private static final String SELECT_ENROLLMENTS = "SELECT" +
            "  Program.displayName," +
            "  Program.enrollmentDateLabel," +
            "  Enrollment.uid," +
            "  Enrollment.enrollmentDate," +
            "  Enrollment.state," +
            "  Enrollment.status," +
            "  InstanceAttribute.label," +
            "  TrackedEntityAttributeValue.value " +
            "FROM (Enrollment INNER JOIN Program ON Program.uid = Enrollment.program)" +
            "  LEFT OUTER JOIN (" +
            "    SELECT TrackedEntityAttribute.uid AS tea, " +
            "           TrackedEntityAttribute.displayName AS label, " +
            "           ProgramTrackedEntityAttribute.displayInList AS showInList, " +
            "           ProgramTrackedEntityAttribute.program AS program, " +
            "           ProgramTrackedEntityAttribute.sortOrder AS formOrder " +
            "    FROM ProgramTrackedEntityAttribute INNER JOIN TrackedEntityAttribute" +
            "      ON TrackedEntityAttribute.uid = ProgramTrackedEntityAttribute.trackedEntityAttribute" +
            "    ) AS InstanceAttribute ON InstanceAttribute.program = Program.uid " +
            "        AND InstanceAttribute.showInList = 1" +
            "  LEFT OUTER JOIN TrackedEntityAttributeValue" +
            "      ON (TrackedEntityAttributeValue.trackedEntityAttribute = InstanceAttribute.tea " +
            "        AND TrackedEntityAttributeValue.trackedEntityInstance = Enrollment.trackedEntityInstance) " +
            "WHERE Enrollment.trackedEntityInstance = ? AND NOT Enrollment.state = 'TO_DELETE' " +
            "ORDER BY datetime(Enrollment.created) DESC, " +
            "Enrollment.uid ASC, InstanceAttribute.formOrder ASC;";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final String promptProgram;

    @NonNull
    private final String promptEnrollmentStatus;

    @NonNull
    private final String promptEnrollmentDateLabel;

    EnrollmentsRepositoryImpl(@NonNull BriteDatabase briteDatabase,
            @NonNull String promptProgram, @NonNull String promptEnrollmentStatus,
            @NonNull String promptEnrollmentDateLabel) {
        this.briteDatabase = briteDatabase;
        this.promptProgram = promptProgram;
        this.promptEnrollmentStatus = promptEnrollmentStatus;
        this.promptEnrollmentDateLabel = promptEnrollmentDateLabel;
    }

    @NonNull
    @Override
    public Flowable<List<ReportViewModel>> reports(@NonNull String uid) {
        return toV2Flowable(briteDatabase.createQuery(EnrollmentModel.TABLE, SELECT_ENROLLMENTS, uid)
                .mapToList(this::mapToPairs))
                .switchMap(rows -> Flowable.fromIterable(rows)
                        .groupBy(Pair::val0, Pair::val1)
                        .concatMap(group -> group.toList().toFlowable()
                                .map(values -> {
                                    // program display name (first item)
                                    values.add(0, value(promptProgram, group.getKey().val0()));

                                    // enrollment date (second item)
                                    values.add(1, value(group.getKey().val1(), group.getKey().val3()));

                                    // enrollment status (last item)
                                    values.add(value(promptEnrollmentStatus, group.getKey().val5()));
                                    return ReportViewModel.create(fromState(group.getKey().val4()),
                                            group.getKey().val2(), StringUtils.join(values));
                                }))
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

    private Pair<Sextet<String, String, String, String, String, String>, String> mapToPairs(@NonNull Cursor cursor) {
        String programDisplayName = string(cursor, 0, "-");
        String programEnrollmentDateLabel = string(cursor, 1, promptEnrollmentDateLabel);
        String enrollment = cursor.getString(2);
        String enrollmentDate = string(cursor, 3, "-");
        String enrollmentState = string(cursor, 4, "-");
        String enrollmentStatus = string(cursor, 5, "-");
        String trackedEntityAttribute = cursor.getString(6);
        String trackedEntityAttributeValue = string(cursor, 7, "-");

        String label = "";
        if (trackedEntityAttribute != null) {
            label = String.format(Locale.US, "%s: %s", trackedEntityAttribute, trackedEntityAttributeValue);
        }

        return Pair.create(Sextet.create(programDisplayName, programEnrollmentDateLabel, enrollment,
                enrollmentDate, enrollmentState, enrollmentStatus), label);
    }

    @NonNull
    private static String value(@NonNull String key, @NonNull String value) {
        return String.format(Locale.US, "%s: %s", key, value);
    }
}
