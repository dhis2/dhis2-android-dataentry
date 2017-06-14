package org.hisp.dhis.android.dataentry.create;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import io.reactivex.observers.TestObserver;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.dataentry.commons.utils.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class SingleEventRepositoryIntegrationTests {
    private static final String CURRENT_DATE = "2016-04-06T00:05:57.495";
    private static final String[] EVENT_PROJECTION = {
            EventModel.Columns.UID,
            EventModel.Columns.CREATED,
            EventModel.Columns.LAST_UPDATED,
            EventModel.Columns.EVENT_DATE,
            EventModel.Columns.PROGRAM,
            EventModel.Columns.PROGRAM_STAGE,
            EventModel.Columns.ORGANISATION_UNIT,
            EventModel.Columns.STATUS,
            EventModel.Columns.STATE
    };


    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private CreateItemsRepository createItemsRepository;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();

        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, "organisation_unit_uid");
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, "program_uid");
        db.insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = new ContentValues();
        programStage.put(ProgramStageModel.Columns.UID, "ps_uid");
        programStage.put(ProgramStageModel.Columns.PROGRAM, "program_uid");
        db.insert(ProgramStageModel.TABLE, null, programStage);

        Date currentDate = BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE);

        CodeGenerator codeGenerator = () -> "test_code";
        CurrentDateProvider currentDateProvider = () -> currentDate;
        createItemsRepository = new SingleEventRepositoryImpl(databaseRule.briteDatabase(),
                codeGenerator, currentDateProvider);
    }

    @Test
    public void saveMustPersistEventWithCorrectProperties() {
        TestObserver<String> testObserver = createItemsRepository.save(
                "organisation_unit_uid", "program_uid").test();

        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.assertValueCount(1);

        assertThat(testObserver.values().get(0)).isEqualTo("test_code");

        assertThatCursor(databaseRule.database()
                .query(EventModel.TABLE, EVENT_PROJECTION, null, null, null, null, null))
                .hasRow("test_code", CURRENT_DATE, CURRENT_DATE, CURRENT_DATE,
                        "program_uid", "ps_uid", "organisation_unit_uid",
                        EventStatus.ACTIVE.name(), State.TO_POST.name())
                .isExhausted();
    }
}
