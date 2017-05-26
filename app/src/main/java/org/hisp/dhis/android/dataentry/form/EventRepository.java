package org.hisp.dhis.android.dataentry.form;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionModel;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
class EventRepository implements FormRepository {
    private static final List<String> TITLE_TABLES = Arrays.asList(
            ProgramModel.TABLE, ProgramStageModel.TABLE);

    private static final List<String> SECTION_TABLES = Arrays.asList(
            EventModel.TABLE, ProgramModel.TABLE, ProgramStageModel.TABLE, ProgramStageSectionModel.TABLE);

    private static final String SELECT_TITLE = "SELECT\n" +
            "  Program.displayName,\n" +
            "  ProgramStage.displayName\n" +
            "FROM Event\n" +
            "  JOIN Program ON Event.program = Program.uid\n" +
            "  JOIN ProgramStage ON Event.programStage = ProgramStage.uid\n" +
            "WHERE Event.uid = ?";

    private static final String SELECT_SECTIONS = "SELECT\n" +
            "  Program.uid AS programUid,\n" +
            "  ProgramStage.uid AS programStageUid,\n" +
            "  ProgramStageSection.uid AS programStageSectionUid,\n" +
            "  ProgramStageSection.displayName AS programStageDisplayName\n" +
            "FROM Event\n" +
            "  JOIN Program ON Event.program = Program.uid\n" +
            "  JOIN ProgramStage ON Event.programStage = ProgramStage.uid\n" +
            "  LEFT OUTER JOIN ProgramStageSection ON ProgramStageSection.programStage = Event.programStage\n" +
            "WHERE Event.uid = ?";

    private static final String SELECT_EVENT_DATE = "SELECT\n" +
            "  Event.eventDate\n" +
            "FROM Event\n" +
            "WHERE Event.uid = ?";

    private static final String SELECT_EVENT_STATUS = "SELECT\n" +
            "  Event.status\n" +
            "FROM Event\n" +
            "WHERE Event.uid = ?";

    @NonNull
    private final BriteDatabase briteDatabase;

    EventRepository(@NonNull BriteDatabase briteDatabase) {
        this.briteDatabase = briteDatabase;
    }

    @NonNull
    @Override
    public Flowable<String> title(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(TITLE_TABLES, SELECT_TITLE, uid)
                .mapToOne(cursor -> cursor.getString(0) + " - " + cursor.getString(1)))
                .distinctUntilChanged();
    }

    @NonNull
    @Override
    public Flowable<String> reportDate(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(EventModel.TABLE, SELECT_EVENT_DATE, uid)
                .mapToOne(cursor -> cursor.getString(0) == null ? "" : cursor.getString(0)))
                .distinctUntilChanged();
    }

    @NonNull
    @Override
    public Flowable<EventStatus> reportStatus(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(EventModel.TABLE, SELECT_EVENT_STATUS, uid)
                .mapToOne(cursor -> EventStatus.valueOf(cursor.getString(0))))
                .distinctUntilChanged();
    }

    @NonNull
    @Override
    public Flowable<List<FormSectionViewModel>> sections(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(SECTION_TABLES, SELECT_SECTIONS, uid)
                .mapToList(cursor -> mapToFormSectionViewModels(uid, cursor))
                .distinctUntilChanged());
    }

    @NonNull
    @Override
    public Consumer<String> storeReportDate(@NonNull String uid) {
        return reportDate -> {
            ContentValues event = new ContentValues();
            event.put(EventModel.Columns.EVENT_DATE, reportDate);
            briteDatabase.update(EventModel.TABLE, event, EventModel.Columns.UID + " = ?", uid);
        };
    }

    @NonNull
    @Override
    public Consumer<EventStatus> storeEventStatus(@NonNull String uid) {
        return eventStatus -> {
            ContentValues event = new ContentValues();
            event.put(EventModel.Columns.STATUS, eventStatus.name());
            briteDatabase.update(EventModel.TABLE, event, EventModel.Columns.UID + " = ?", uid);
        };
    }

    @NonNull
    private FormSectionViewModel mapToFormSectionViewModels(
            @NonNull String eventUid, @NonNull Cursor cursor) {
        if (cursor.getString(2) == null) {
            // This programstage has no sections
            return FormSectionViewModel.createForProgramStage(
                    eventUid, cursor.getString(1));
        } else {
            // This programstage has sections
            return FormSectionViewModel.createForSection(
                    eventUid, cursor.getString(2), cursor.getString(3));
        }
    }
}