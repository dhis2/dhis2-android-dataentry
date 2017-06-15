package org.hisp.dhis.android.dataentry.create;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
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

    private static final String PROGRAM_UID = "program_uid";
    private static final String ORGANISATION_UNIT_UID = "organisation_unit_uid";
    private static final String PS_UID = "ps_uid";
    private static final String TEST_CODE = "test_code";

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private CreateItemsRepository createItemsRepository;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();

        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, ORGANISATION_UNIT_UID);
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, PROGRAM_UID);
        db.insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = new ContentValues();
        programStage.put(ProgramStageModel.Columns.UID, PS_UID);
        programStage.put(ProgramStageModel.Columns.PROGRAM, PROGRAM_UID);
        db.insert(ProgramStageModel.TABLE, null, programStage);

        Date currentDate = BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE);

        CodeGenerator codeGenerator = () -> TEST_CODE;
        CurrentDateProvider currentDateProvider = () -> currentDate;
        createItemsRepository = new SingleEventRepositoryImpl(databaseRule.briteDatabase(),
                codeGenerator, currentDateProvider);
    }

    @Test
    public void saveMustPersistEventWithCorrectProperties() {
        TestObserver<String> testObserver = createItemsRepository.save(
                ORGANISATION_UNIT_UID, PROGRAM_UID).test();

        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.assertValueCount(1);

        assertThat(testObserver.values().get(0)).isEqualTo(TEST_CODE);

        assertThatCursor(databaseRule.database()
                .query(EventModel.TABLE, EVENT_PROJECTION, null, null, null, null, null))
                .hasRow(TEST_CODE, CURRENT_DATE, CURRENT_DATE, CURRENT_DATE,
                        PROGRAM_UID, PS_UID, ORGANISATION_UNIT_UID,
                        EventStatus.ACTIVE.name(), State.TO_POST.name())
                .isExhausted();
    }


    @Test
    public void errorsMustBePropagatedToConsumer() {
        TestObserver<String> testObserver = createItemsRepository.save(
                "non_existing_organisation_unit", PROGRAM_UID).test();

        assertThat(testObserver.errors().get(0))
                .isInstanceOf(SQLiteConstraintException.class);
        testObserver.assertValueCount(0);
    }
}
