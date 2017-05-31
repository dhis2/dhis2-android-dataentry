package org.hisp.dhis.android.dataentry.dashboard;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.event.EventStatus;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

class DashboardRepositoryImpl implements DashboardRepository {

    private static String EVENTS_QUERY = "SELECT\n" +
            "  Event.uid,\n" +
            "  ProgramStage.displayName,\n" +
            "  Event.eventDate,\n" +
            "  Event.status\n" +
            "FROM Event\n" +
            "  JOIN ProgramStage ON Event.programStage = ProgramStage.uid\n" +
            "  WHERE Event.enrollment = ?";

    private static List<String> EVENT_TABLES = Arrays.asList("Event", "ProgramStage");

    @NonNull
    private final BriteDatabase briteDataBase;

    DashboardRepositoryImpl(@NonNull BriteDatabase briteDatabase) {
        this.briteDataBase = briteDatabase;
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