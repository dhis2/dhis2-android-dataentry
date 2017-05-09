package org.hisp.dhis.android.dataentry.form;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionModel;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryViewArguments;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

class EventRepositoryImpl implements FormRepository {

    private static final List<String> TITLE_TABLES = Arrays.asList(ProgramModel.TABLE, ProgramStageModel.TABLE);

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

    @NonNull
    private final BriteDatabase briteDatabase;

    EventRepositoryImpl(@NonNull BriteDatabase briteDatabase) {
        this.briteDatabase = briteDatabase;
    }

    @NonNull
    @Override
    public Flowable<String> title(@NonNull String uid) {
        return toV2Flowable(briteDatabase
                .createQuery(TITLE_TABLES, SELECT_TITLE, uid)
                .mapToOne(cursor -> cursor.getString(0) + " - " + cursor.getString(1)));
    }

    @NonNull
    @Override
    public Flowable<List<DataEntryViewArguments>> sections(@NonNull String uid) {

        return toV2Flowable(briteDatabase.createQuery(SECTION_TABLES, SELECT_SECTIONS, uid)
                .mapToList(cursor -> mapToDataEntryViewArguments(uid, cursor)));
    }

    @NonNull
    private DataEntryViewArguments mapToDataEntryViewArguments(@NonNull String eventUid, @NonNull Cursor cursor) {
        if (cursor.getString(2) != null) {
            // This programstage has sections
            return DataEntryViewArguments.createForSection(eventUid, cursor.getString(2), cursor.getString(3));
        } else {
            // This programstage has no sections
            return DataEntryViewArguments.createForProgramStage(eventUid, cursor.getString(1));
        }
    }
}