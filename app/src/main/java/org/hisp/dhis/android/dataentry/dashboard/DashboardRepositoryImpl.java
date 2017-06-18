package org.hisp.dhis.android.dataentry.dashboard;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

class DashboardRepositoryImpl implements DashboardRepository {
    private static final String ATTRIBUTES_QUERY = "SELECT DISTINCT\n" +
            "  TrackedEntityAttribute.displayName,\n" +
            "  TrackedEntityAttributeValue.value\n" +
            "FROM TrackedEntityAttribute\n" +
            "  LEFT JOIN Enrollment ON Enrollment.uid = ?\n" +
            "  LEFT JOIN TrackedEntityAttributeValue\n" +
            "    ON Enrollment.trackedEntityInstance = TrackedEntityAttributeValue.trackedEntityInstance\n" +
            "  AND TrackedEntityAttributeValue.trackedEntityAttribute = TrackedEntityAttribute.uid\n" +
            "WHERE TrackedEntityAttribute.displayInListNoProgram = 1\n" +
            "ORDER BY TrackedEntityAttribute.sortOrderInListNoProgram\n" +
            "LIMIT 2";

    private static final List<String> ATTRIBUTE_TABLES = Arrays.asList(
            TrackedEntityAttributeModel.TABLE, EnrollmentModel.TABLE, TrackedEntityAttributeValueModel.TABLE);

    private static final String EVENTS_QUERY = "SELECT\n" +
            "  Event.uid,\n" +
            "  ProgramStage.displayName,\n" +
            "  Event.eventDate,\n" +
            "  Event.status\n" +
            "FROM Event\n" +
            "  JOIN ProgramStage ON Event.programStage = ProgramStage.uid\n" +
            "  WHERE Event.enrollment = ?" +
            "ORDER BY Event.eventDate DESC";

    private static final String SELECT_ENROLLMENT = "SELECT " + EnrollmentModel.Columns.PROGRAM +
            " FROM " + EnrollmentModel.TABLE +
            " WHERE " + EnrollmentModel.Columns.UID + " =? " +
            " LIMIT 1;";

    private static final List<String> EVENT_TABLES = Arrays.asList(EventModel.TABLE, ProgramStageModel.TABLE);

    @NonNull
    private final BriteDatabase briteDataBase;

    DashboardRepositoryImpl(@NonNull BriteDatabase briteDatabase) {
        this.briteDataBase = briteDatabase;
    }

    @NonNull
    @Override
    public Flowable<String> program(@NonNull String enrollmentUid) {
        return toV2Flowable(briteDataBase.createQuery(EnrollmentModel.TABLE,
                SELECT_ENROLLMENT, enrollmentUid)
                .mapToOne(cursor -> cursor.getString(0))
                .take(1));
    }

    @NonNull
    @Override
    public Flowable<List<String>> attributes(@NonNull String enrollmentUid) {
        return toV2Flowable(briteDataBase
                .createQuery(ATTRIBUTE_TABLES, ATTRIBUTES_QUERY, enrollmentUid)
                .mapToList(cursor -> cursor.getString(0) + ": " + cursor.getString(1))
                .distinctUntilChanged());
    }

    @NonNull
    @Override
    public Flowable<List<EventViewModel>> events(@NonNull String enrollmentUid) {
        return toV2Flowable(briteDataBase
                .createQuery(EVENT_TABLES, EVENTS_QUERY, enrollmentUid)
                .mapToList(cursor -> EventViewModel
                        .create(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                                EventStatus.valueOf(cursor.getString(3)))));
    }
}