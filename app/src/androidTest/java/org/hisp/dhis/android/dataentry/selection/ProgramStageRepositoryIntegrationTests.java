package org.hisp.dhis.android.dataentry.selection;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
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
public class ProgramStageRepositoryIntegrationTests {
    private static final String PROGRAM_UID = "program_uid";
    private static final String PROGRAM_DISPLAY_NAME = "program_display_name";
    private static final String PROGRAM_STAGE_1_UID = "program_stage_uid";
    private static final String PROGRAM_STAGE_2_UID = "program_stage_2_uid";
    private static final String PROGRAM_STAGE_3_UID = "program_stage_3_uid";
    private static final String PROGRAM_STAGE_1_DISPLAY_NAME = "program_stage_display_name";
    private static final String PROGRAM_STAGE_2_DISPLAY_NAME = "program_stage_2_name";
    private static final String PROGRAM_STAGE_3_DISPLAY_NAME = "program_stage_3_display_name";

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    // under tests
    private SelectionRepository repository;

    @Before
    public void setup() {
        SQLiteDatabase database = databaseRule.database();

        database.insert(ProgramModel.TABLE, null, program(PROGRAM_UID, PROGRAM_DISPLAY_NAME));
        database.insert(ProgramStageModel.TABLE, null, programStage(PROGRAM_STAGE_1_UID,
                PROGRAM_STAGE_1_DISPLAY_NAME, PROGRAM_UID));
        database.insert(ProgramStageModel.TABLE, null, programStage(PROGRAM_STAGE_2_UID,
                PROGRAM_STAGE_2_DISPLAY_NAME, PROGRAM_UID));

        repository = new ProgramStageRepositoryImpl(databaseRule.briteDatabase(), PROGRAM_UID);
    }

    @Test
    public void searchMustReturnAllMatchingStages() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        // happy path test: verify that one OptionSet with two programs is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_1_UID,
                PROGRAM_STAGE_1_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_2_UID,
                PROGRAM_STAGE_2_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustReturnAllMatchingStagesOnEmptyQuery() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("").test();

        // happy path test: verify that one OptionSet with two programs is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_1_UID,
                PROGRAM_STAGE_1_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_2_UID,
                PROGRAM_STAGE_2_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustNotReturnNonMatchingStages() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("random_stage").test();

        // happy path test: verify that one OptionSet with two programs is in there.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void searchMustObserveUpdatesInStagesTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        // change name of programStage & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().update(ProgramStageModel.TABLE, programStage(PROGRAM_STAGE_2_UID,
                "updated_program2", PROGRAM_UID), ProgramStageModel.Columns.UID + "=?", PROGRAM_STAGE_2_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_1_UID,
                PROGRAM_STAGE_1_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_2_UID,
                "updated_program2"))).isTrue();
    }

    @Test
    public void searchMustObserveInsertsInOptionTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        // add an programStage & verify that it happens.
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().insert(ProgramStageModel.TABLE, programStage(PROGRAM_STAGE_3_UID,
                PROGRAM_STAGE_3_DISPLAY_NAME, PROGRAM_UID));

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_1_UID,
                PROGRAM_STAGE_1_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_2_UID,
                PROGRAM_STAGE_2_DISPLAY_NAME))).isTrue();
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_3_UID,
                PROGRAM_STAGE_3_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustObserveDeletesInStagesTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();
        // delete the programStage & verify that it is observed.

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().delete(ProgramStageModel.TABLE,
                ProgramStageModel.Columns.UID + "=?", PROGRAM_STAGE_2_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(SelectionViewModel.create(PROGRAM_STAGE_1_UID,
                PROGRAM_STAGE_1_DISPLAY_NAME))).isTrue();
    }

    @Test
    public void searchMustObserveParentTable() {
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        // delete the parent and verify that fk constraints are updated accordingly...
        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        databaseRule.briteDatabase().delete(ProgramModel.TABLE, ProgramModel.Columns.UID + "=?",
                PROGRAM_UID);

        subscriber.assertValueCount(2);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(1);
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void searchMustReturnEmptyListOnWrongParent() {
        repository = new ProgramStageRepositoryImpl(databaseRule.briteDatabase(), "wrong");

        // try to retrieve program that is not in db
        TestSubscriber<List<SelectionViewModel>> subscriber = repository.search("program").test();

        subscriber.assertValueCount(1);
        subscriber.assertNoErrors();
        subscriber.assertNotComplete();

        List<SelectionViewModel> result = subscriber.values().get(0);
        assertThat(result.size()).isEqualTo(0);
    }

    private ContentValues program(String uid, String displayName) {
        ContentValues values = new ContentValues();
        values.put(ProgramModel.Columns.UID, uid);
        values.put(ProgramModel.Columns.CREATED, new Date().toString());
        values.put(ProgramModel.Columns.LAST_UPDATED, new Date().toString());
        values.put(ProgramModel.Columns.CODE, "test_code");
        values.put(ProgramModel.Columns.NAME, "test_name");
        values.put(ProgramModel.Columns.DISPLAY_NAME, displayName);
        values.put(ProgramModel.Columns.SHORT_NAME, "test_short_name");
        values.put(ProgramModel.Columns.DISPLAY_SHORT_NAME, "test_display_short_name");
        values.put(ProgramModel.Columns.DESCRIPTION, "test_description");
        values.put(ProgramModel.Columns.DISPLAY_DESCRIPTION, "test_display_description");
        values.put(ProgramModel.Columns.VERSION, 1);
        values.put(ProgramModel.Columns.ONLY_ENROLL_ONCE, true);
        values.put(ProgramModel.Columns.ENROLLMENT_DATE_LABEL, "enrollment date");
        values.put(ProgramModel.Columns.DISPLAY_INCIDENT_DATE, true);
        values.put(ProgramModel.Columns.INCIDENT_DATE_LABEL, "incident date label");
        values.put(ProgramModel.Columns.REGISTRATION, true);
        values.put(ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE, true);
        values.put(ProgramModel.Columns.DATA_ENTRY_METHOD, true);
        values.put(ProgramModel.Columns.IGNORE_OVERDUE_EVENTS, false);
        values.put(ProgramModel.Columns.RELATIONSHIP_FROM_A, true);
        values.put(ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE, true);
        values.put(ProgramModel.Columns.CAPTURE_COORDINATES, true);
        values.put(ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION, true);
        values.put(ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST, true);
        values.put(ProgramModel.Columns.PROGRAM_TYPE, ProgramType.WITH_REGISTRATION.name());
        values.putNull(ProgramModel.Columns.RELATIONSHIP_TYPE);
        values.put(ProgramModel.Columns.RELATIONSHIP_TEXT, "test relationship");
        values.putNull(ProgramModel.Columns.RELATED_PROGRAM);
        values.putNull(ProgramModel.Columns.TRACKED_ENTITY);
        return values;
    }

    private ContentValues programStage(String uid, String displayName, String programUid) {
        ContentValues values = new ContentValues();
        values.put(ProgramModel.Columns.UID, uid);
        values.put(ProgramStageModel.Columns.DISPLAY_NAME, displayName);
        values.put(ProgramStageModel.Columns.PROGRAM, programUid);
        values.put(ProgramModel.Columns.CREATED, new Date().toString());
        values.put(ProgramModel.Columns.LAST_UPDATED, new Date().toString());
        values.put(ProgramStageModel.Columns.CODE, "test_code");
        values.put(ProgramStageModel.Columns.NAME, "test_name");
        values.put(ProgramStageModel.Columns.EXECUTION_DATE_LABEL, "test_executionDateLabel");
        values.put(ProgramStageModel.Columns.ALLOW_GENERATE_NEXT_VISIT, 0);
        values.put(ProgramStageModel.Columns.VALID_COMPLETE_ONLY, 0);
        values.put(ProgramStageModel.Columns.REPORT_DATE_TO_USE, "test_reportDateToUse");
        values.put(ProgramStageModel.Columns.OPEN_AFTER_ENROLLMENT, 0);
        values.put(ProgramStageModel.Columns.REPEATABLE, 0);
        values.put(ProgramStageModel.Columns.CAPTURE_COORDINATES, 1);
        values.put(ProgramStageModel.Columns.FORM_TYPE, FormType.DEFAULT.name());
        values.put(ProgramStageModel.Columns.DISPLAY_GENERATE_EVENT_BOX, 1);
        values.put(ProgramStageModel.Columns.GENERATED_BY_ENROLMENT_DATE, 1);
        values.put(ProgramStageModel.Columns.AUTO_GENERATE_EVENT, 0);
        values.put(ProgramStageModel.Columns.SORT_ORDER, 0);
        values.put(ProgramStageModel.Columns.HIDE_DUE_DATE, 1);
        values.put(ProgramStageModel.Columns.BLOCK_ENTRY_FORM, 0);
        values.put(ProgramStageModel.Columns.MIN_DAYS_FROM_START, 5);
        values.put(ProgramStageModel.Columns.STANDARD_INTERVAL, 7);
        return values;
    }
}
