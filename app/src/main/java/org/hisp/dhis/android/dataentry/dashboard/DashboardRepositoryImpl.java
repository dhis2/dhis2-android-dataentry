package org.hisp.dhis.android.dataentry.dashboard;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.dataentry.commons.utils.DateUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;
import timber.log.Timber;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

class DashboardRepositoryImpl implements DashboardRepository {

    private static final String ATTRIBUTES_QUERY = "SELECT " +
            "  TrackedEntityAttribute.displayName, " +
            "  TrackedEntityAttributeValue.value " +
            "FROM Enrollment " +
            "  INNER JOIN ProgramTrackedEntityAttribute ON Enrollment.program = " +
            " ProgramTrackedEntityAttribute.program " +
            "  INNER JOIN TrackedEntityAttribute ON ProgramTrackedEntityAttribute.trackedEntityAttribute = " +
            "TrackedEntityAttribute.uid " +
            "                                       AND ProgramTrackedEntityAttribute.displayInList = 1 " +
            "  LEFT JOIN TrackedEntityAttributeValue " +
            "    ON TrackedEntityAttribute.uid = TrackedEntityAttributeValue.trackedEntityAttribute " +
            "       AND Enrollment.trackedEntityInstance = TrackedEntityAttributeValue.trackedEntityInstance " +
            "WHERE Enrollment.uid = ? " +
            "ORDER BY ProgramTrackedEntityAttribute.sortOrder " +
            "LIMIT 2";

    private static final List<String> ATTRIBUTE_TABLES = Arrays.asList(
            ProgramTrackedEntityAttributeModel.TABLE, TrackedEntityAttributeModel.TABLE, EnrollmentModel.TABLE,
            TrackedEntityAttributeValueModel.TABLE);

    private static final String EVENTS_QUERY = "SELECT " +
            "  Event.uid, " +
            "  ProgramStage.displayName, " +
            "  Event.eventDate, " +
            "  Event.status " +
            "FROM Event " +
            "  JOIN ProgramStage ON Event.programStage = ProgramStage.uid " +
            "  WHERE Event.enrollment = ?" +
            "ORDER BY Event.eventDate DESC";

    private static final List<String> EVENT_TABLES = Arrays.asList(EventModel.TABLE, ProgramStageModel.TABLE);

    @NonNull
    private final BriteDatabase briteDataBase;

    DashboardRepositoryImpl(@NonNull BriteDatabase briteDatabase) {
        this.briteDataBase = briteDatabase;
    }

    @NonNull
    @Override
    public Flowable<List<String>> attributes(@NonNull String enrollmentUid) {
        return toV2Flowable(briteDataBase
                .createQuery(ATTRIBUTE_TABLES, ATTRIBUTES_QUERY, enrollmentUid)
                .mapToList(this::mapToAttributes)
                .distinctUntilChanged());
    }

    private String mapToAttributes(Cursor cursor) {
        String value = cursor.getString(1) == null ? "-" : cursor.getString(1);
        return String.format(Locale.US, "%s: %s", cursor.getString(0), value);
    }

    @NonNull
    @Override
    public Flowable<List<EventViewModel>> events(@NonNull String enrollmentUid) {
        return toV2Flowable(briteDataBase
                .createQuery(EVENT_TABLES, EVENTS_QUERY, enrollmentUid)
                .mapToList(cursor -> EventViewModel
                        .create(cursor.getString(0), cursor.getString(1), formatDate(cursor.getString(2)),
                                EventStatus.valueOf(cursor.getString(3)))));
    }

    private String formatDate(String date) {
        try {
            return DateUtils.uiDateFormat().format(DateUtils.databaseDateFormat().parse(date));
        } catch (ParseException e) {
            Timber.e(e, "DashboardRepository - Unable to parse date. Expected format: " +
                    DateUtils.databaseDateFormat().toPattern() + ". Input: " + date);
            return date;
        }
    }
}