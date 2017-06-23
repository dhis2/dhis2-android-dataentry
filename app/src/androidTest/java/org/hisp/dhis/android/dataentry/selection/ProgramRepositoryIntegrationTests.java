package org.hisp.dhis.android.dataentry.selection;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramModel.Columns;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class ProgramRepositoryIntegrationTests {
    private static final String ORGUNIT_DISPLAY_NAME = "program_set_display_name";
    private static final String PROGRAM_DISPLAY_NAME = "program_display_name";
    private static final String ORGUNIT_UID = "orgunit_uid";
    private static final String PROGRAM_UID = "program_uid";
    private static final String PROGRAM_2_UID = "program_2_uid";
    private static final String PROGRAM_3_UID = "program_3_uid";
    private static final String PROGRAM_2_DISPLAY_NAME = "program_2_name";
    private static final String PROGRAM_3_DISPLAY_NAME = "program_3_display_name";
    public static final int WITH_REG = 1;
    public static final int WITHOUT_REG = 0;

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    // under tests
    private SelectionRepository repository;

    @Before
    public void setup() {
        SQLiteDatabase database = databaseRule.database();
        repository = new ProgramRepositoryImpl(databaseRule.briteDatabase(), ORGUNIT_UID, WITH_REG);

        database.insert(OrganisationUnitModel.TABLE, null, orgUnit(ORGUNIT_UID, ORGUNIT_DISPLAY_NAME));

        database.insert(ProgramModel.TABLE, null, program(PROGRAM_UID, PROGRAM_DISPLAY_NAME, true));
        database.insert(OrganisationUnitProgramLinkModel.TABLE, null,
                programOrgUnitLink(PROGRAM_UID, ORGUNIT_UID));

        database.insert(ProgramModel.TABLE, null, program(PROGRAM_2_UID, PROGRAM_2_DISPLAY_NAME, true));
        database.insert(OrganisationUnitProgramLinkModel.TABLE, null,
                programOrgUnitLink(PROGRAM_2_UID, ORGUNIT_UID));
    }

    @Test
    public void searchMustReturnAllMatchingPrograms() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        // happy path test: verify that one OptionSet with two programs is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_UID, PROGRAM_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_2_UID, PROGRAM_2_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustReturnAllMatchingProgramsOfType() {
        ProgramRepositoryImpl repository = new ProgramRepositoryImpl(databaseRule.briteDatabase(), ORGUNIT_UID,
                WITHOUT_REG);

        databaseRule.database().insert(ProgramModel.TABLE, null,
                program(PROGRAM_3_UID, PROGRAM_3_DISPLAY_NAME, false));
        databaseRule.database().insert(OrganisationUnitProgramLinkModel.TABLE, null,
                programOrgUnitLink(PROGRAM_3_UID, ORGUNIT_UID));

        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_3_UID, PROGRAM_3_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_UID, PROGRAM_DISPLAY_NAME))).isFalse();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_2_UID, PROGRAM_2_DISPLAY_NAME))).isFalse();
    }

    @Test
    public void searchMustReturnAllProgramsOnEmptyQuery() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("").test();

        // happy path test: verify that one OptionSet with two programs is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_UID, PROGRAM_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_2_UID, PROGRAM_2_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustNotReturnNonMatchingPrograms() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("random_program").test();

        // happy path test: verify that one OptionSet with two options is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void searchMustObserveUpdatesInProgramTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        // change name of program & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().update(ProgramModel.TABLE, program(PROGRAM_2_UID, "updated_program2", true),
                ProgramModel.Columns.UID + "=?", PROGRAM_2_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_UID, PROGRAM_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_2_UID, "updated_program2"))).isTrue();
    }

    @Test
    public void searchMustObserveInsertsInProgramTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        // add an program & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().insert(ProgramModel.TABLE, program(PROGRAM_3_UID, PROGRAM_3_DISPLAY_NAME, true));
        databaseRule.briteDatabase().insert(OrganisationUnitProgramLinkModel.TABLE,
                programOrgUnitLink(PROGRAM_3_UID, ORGUNIT_UID));

        subscriber.assertValueCount(3);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_UID, PROGRAM_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_2_UID, PROGRAM_2_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_3_UID, PROGRAM_3_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustObserveDeletesInProgramTable() {
        // delete the program & verify that it is observed.
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + "=?", PROGRAM_2_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_UID, PROGRAM_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustObserveParentTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        // delete the parent and verify that fk constraints are updated accordingly...
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().delete(OrganisationUnitModel.TABLE, OrganisationUnitModel.Columns.UID + "=?",
                ORGUNIT_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void searchMustReturnEmptyListOnWrongParent() {
        repository = new OptionSetRepositoryImpl(databaseRule.briteDatabase(), "wrong");
        // try to retrieve orgUnit that is not in db
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(0);
    }

    private ContentValues orgUnit(String orgunitUid, String orgunitName) {
        ContentValues result = new ContentValues();
        result.put(OrganisationUnitModel.Columns.UID, orgunitUid);
        result.put(OrganisationUnitModel.Columns.CODE, "orgUnitCode");
        result.put(OrganisationUnitModel.Columns.NAME, "orgUnitName");
        result.put(OrganisationUnitModel.Columns.DISPLAY_NAME, orgunitName);
        result.put(OrganisationUnitModel.Columns.CREATED, new Date().toString());
        result.put(OrganisationUnitModel.Columns.LAST_UPDATED, new Date().toString());
        result.put(OrganisationUnitModel.Columns.SHORT_NAME, "orgUnitName");
        result.put(OrganisationUnitModel.Columns.DISPLAY_SHORT_NAME, "orgUnitShortName");
        result.put(OrganisationUnitModel.Columns.DESCRIPTION, "description");
        result.put(OrganisationUnitModel.Columns.DISPLAY_DESCRIPTION, "displayDescription");
        result.put(OrganisationUnitModel.Columns.PATH, "path");
        result.put(OrganisationUnitModel.Columns.OPENING_DATE, new Date().toString());
        result.put(OrganisationUnitModel.Columns.CLOSED_DATE, new Date().toString());
        result.put(OrganisationUnitModel.Columns.PARENT, "parent");
        result.put(OrganisationUnitModel.Columns.LEVEL, 1);
        return result;
    }

    private ContentValues program(String uid, String displayName, Boolean registration) {
        ContentValues values = new ContentValues();
        values.put(Columns.UID, uid);
        values.put(Columns.CREATED, new Date().toString());
        values.put(Columns.LAST_UPDATED, new Date().toString());
        values.put(Columns.CODE, "test_code");
        values.put(Columns.NAME, "test_name");
        values.put(Columns.DISPLAY_NAME, displayName);
        values.put(Columns.SHORT_NAME, "test_short_name");
        values.put(Columns.DISPLAY_SHORT_NAME, "test_display_short_name");
        values.put(Columns.DESCRIPTION, "test_description");
        values.put(Columns.DISPLAY_DESCRIPTION, "test_display_description");
        values.put(Columns.VERSION, 1);
        values.put(Columns.ONLY_ENROLL_ONCE, true);
        values.put(Columns.ENROLLMENT_DATE_LABEL, "enrollment date");
        values.put(Columns.DISPLAY_INCIDENT_DATE, true);
        values.put(Columns.INCIDENT_DATE_LABEL, "incident date label");
        values.put(Columns.REGISTRATION, registration);
        values.put(Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE, true);
        values.put(Columns.DATA_ENTRY_METHOD, true);
        values.put(Columns.IGNORE_OVERDUE_EVENTS, false);
        values.put(Columns.RELATIONSHIP_FROM_A, true);
        values.put(Columns.SELECT_INCIDENT_DATES_IN_FUTURE, true);
        values.put(Columns.CAPTURE_COORDINATES, true);
        values.put(Columns.USE_FIRST_STAGE_DURING_REGISTRATION, true);
        values.put(Columns.DISPLAY_FRONT_PAGE_LIST, true);
        values.put(Columns.PROGRAM_TYPE, ProgramType.WITH_REGISTRATION.name());
        values.putNull(Columns.RELATIONSHIP_TYPE);
        values.put(Columns.RELATIONSHIP_TEXT, "test relationship");
        values.putNull(Columns.RELATED_PROGRAM);
        values.putNull(Columns.TRACKED_ENTITY);
        return values;
    }

    private ContentValues programOrgUnitLink(String programUid, String orgUnitUid) {
        ContentValues values = new ContentValues();
        values.put(OrganisationUnitProgramLinkModel.Columns.PROGRAM, programUid);
        values.put(OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT, orgUnitUid);
        return values;
    }
}
