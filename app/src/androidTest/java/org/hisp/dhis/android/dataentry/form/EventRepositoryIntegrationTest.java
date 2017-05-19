package org.hisp.dhis.android.dataentry.form;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class EventRepositoryIntegrationTest {

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());
    private SQLiteDatabase db;

    FormRepository formRepository;

    @Before
    public void setUp() throws Exception {
        databaseRule.insertMetaData();
        db = databaseRule.database();
        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, "org_unit_uid");
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, "program_uid");
        program.put(ProgramModel.Columns.DISPLAY_NAME, "Program");
        db.insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = new ContentValues();
        programStage.put(ProgramStageModel.Columns.UID, "ps_uid");
        programStage.put(ProgramStageModel.Columns.PROGRAM, "program_uid");
        programStage.put(ProgramStageModel.Columns.DISPLAY_NAME, "Program Stage");
        db.insert(ProgramStageModel.TABLE, null, programStage);

        formRepository = new EventRepository(databaseRule.briteDatabase());
    }

    @Test
    public void titleShouldPropagateCorrectResults() throws Exception {
        db.insert(EventModel.TABLE, null, event("event_uid", "2016-05-11", "org_unit_uid", "program_uid",
                "ps_uid"));

        TestSubscriber<String> testObserver =
                formRepository.title("event_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0)).isEqualTo("Program - Program Stage");

        ContentValues programWithNewName = new ContentValues();
        programWithNewName.put(ProgramModel.Columns.DISPLAY_NAME, "New Program Name");
        databaseRule.briteDatabase().update(
                ProgramModel.TABLE, programWithNewName, "Program.uid = 'program_uid'", null);
        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(1)).isEqualTo("New Program Name - Program Stage");

        ContentValues programStageWithNewName = new ContentValues();
        programStageWithNewName.put(ProgramModel.Columns.DISPLAY_NAME, "New Program Stage Name");
        databaseRule.briteDatabase().update(
                ProgramStageModel.TABLE, programStageWithNewName, "ProgramStage.uid = 'ps_uid'", null);
        testObserver.assertValueCount(3);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(2)).isEqualTo("New Program Name - New Program Stage Name");
    }

    @Test
    public void reportDateShouldPropagateCorrectResults() throws Exception {
        db.insert(EventModel.TABLE, null, event("event_uid", "2016-05-11", "org_unit_uid", "program_uid",
                "ps_uid"));

        TestSubscriber<String> testObserver =
                formRepository.reportDate("event_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0)).isEqualTo("2016-05-11");

        ContentValues eventWithNewDate = new ContentValues();
        eventWithNewDate.put(EventModel.Columns.EVENT_DATE, "2016-05-01");
        databaseRule.briteDatabase().update(EventModel.TABLE, eventWithNewDate, "Event.uid = 'event_uid'", null);
        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(1)).isEqualTo("2016-05-01");
    }

    @Test
    public void reportStatusShouldPropagateCorrectResults() throws Exception {
        db.insert(EventModel.TABLE, null, event("event_uid", "2016-05-11", "org_unit_uid", "program_uid",
                "ps_uid"));

        ContentValues activeEvent = new ContentValues();
        activeEvent.put(EventModel.Columns.STATUS, EventStatus.ACTIVE.name());
        databaseRule.briteDatabase().update(EventModel.TABLE, activeEvent, "Event.uid = 'event_uid'", null);

        TestSubscriber<EventStatus> testObserver =
                formRepository.reportStatus("event_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0)).isEqualTo(EventStatus.ACTIVE);

        ContentValues completedEvent = new ContentValues();
        completedEvent.put(EventModel.Columns.STATUS, EventStatus.COMPLETED.name());
        databaseRule.briteDatabase().update(EventModel.TABLE, completedEvent, "Event.uid = 'event_uid'", null);
        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(1)).isEqualTo(EventStatus.COMPLETED);
    }

    @Test
    public void sectionsShouldPropagateCorrectResults() throws Exception {

        db.insert(EventModel.TABLE, null, event("event_uid", "2016-05-11", "org_unit_uid", "program_uid", "ps_uid"));

        FormSectionViewModel formSectionViewModel =
                FormSectionViewModel.createForProgramStage("event_uid", "ps_uid");

        TestSubscriber<List<FormSectionViewModel>> testObserver = formRepository.sections("event_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0).size()).isEqualTo(1);
        assertThat(testObserver.values().get(0).get(0)).isEqualTo(formSectionViewModel);

        BriteDatabase briteDb = databaseRule.briteDatabase();
        BriteDatabase.Transaction transaction = briteDb.newTransaction();

        ContentValues firstSection = new ContentValues();
        firstSection.put(ProgramStageSectionModel.Columns.UID, "section_one");
        firstSection.put(ProgramStageSectionModel.Columns.DISPLAY_NAME, "Section one");
        firstSection.put(ProgramStageSectionModel.Columns.PROGRAM_STAGE, "ps_uid");
        databaseRule.briteDatabase().insert(ProgramStageSectionModel.TABLE, firstSection);

        ContentValues secondSection = new ContentValues();
        secondSection.put(ProgramStageSectionModel.Columns.UID, "section_two");
        secondSection.put(ProgramStageSectionModel.Columns.DISPLAY_NAME, "Section two");
        secondSection.put(ProgramStageSectionModel.Columns.PROGRAM_STAGE, "ps_uid");
        databaseRule.briteDatabase().insert(ProgramStageSectionModel.TABLE, secondSection);

        transaction.markSuccessful();
        transaction.end();

        FormSectionViewModel firstSectionViewModel =
                FormSectionViewModel.createForSection("event_uid", "section_one", "Section one");
        FormSectionViewModel secondSectionViewModel =
                FormSectionViewModel.createForSection("event_uid", "section_two", "Section two");

        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        assertThat(testObserver.values().get(1).size()).isEqualTo(2);
        assertThat(testObserver.values().get(1).get(0)).isEqualTo(firstSectionViewModel);
        assertThat(testObserver.values().get(1).get(1)).isEqualTo(secondSectionViewModel);
    }

    @Test
    public void reportDateShouldBeStoredCorrectly() throws Exception {
        db.insert(EventModel.TABLE, null, event("event_uid", "2016-05-11", "org_unit_uid", "program_uid", "ps_uid"));
        formRepository.storeReportDate("event_uid").accept("2019-09-09");

        Cursor cursor = db.rawQuery("SELECT Event.eventDate FROM Event WHERE Event.uid = 'event_uid'", null);
        cursor.moveToFirst();
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(cursor.getString(0)).isEqualTo("2019-09-09");
        cursor.close();
    }

    @Test
    public void eventStatusShouldBeStoredCorrectly() throws Exception {
        db.insert(EventModel.TABLE, null, event("event_uid", "2016-05-11", "org_unit_uid", "program_uid", "ps_uid"));
        formRepository.storeEventStatus("event_uid").accept(EventStatus.COMPLETED);

        Cursor cursor = db.rawQuery("SELECT Event.status FROM Event WHERE Event.uid = 'event_uid'", null);
        cursor.moveToFirst();
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(cursor.getString(0)).isEqualTo(EventStatus.COMPLETED.name());
        cursor.close();
    }

    private static ContentValues event(String uid, String eventDate, String orgUnit, String program,
                                       String programStage) {
        ContentValues event = new ContentValues();
        event.put(EventModel.Columns.UID, uid);
        event.put(EventModel.Columns.EVENT_DATE, eventDate);
        event.put(EventModel.Columns.ORGANISATION_UNIT, orgUnit);
        event.put(EventModel.Columns.PROGRAM, program);
        event.put(EventModel.Columns.PROGRAM_STAGE, programStage);
        return event;
    }

}