package org.hisp.dhis.android.dataentry.create;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
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
public class EnrollmentEventRepositoryIntegrationTests {
    private static final String CURRENT_DATE = "2016-04-06T00:05:57.495";
    private static final String[] EVENT_PROJECTION = {
            EventModel.Columns.UID,
            EventModel.Columns.CREATED,
            EventModel.Columns.LAST_UPDATED,
            EventModel.Columns.EVENT_DATE,
            EventModel.Columns.ENROLLMENT_UID,
            EventModel.Columns.PROGRAM,
            EventModel.Columns.PROGRAM_STAGE,
            EventModel.Columns.ORGANISATION_UNIT,
            EventModel.Columns.STATUS,
            EventModel.Columns.STATE
    };

    private static final String TRACKED_ENTITY_UID = "tracked_entity_uid";
    private static final String TEI_UID = "tei_uid";
    private static final String ORGANISATION_UNIT_UID = "organisation_unit_uid";
    private static final String PROGRAM_UID = "program_uid";
    private static final String PS_UID = "ps_uid";
    private static final String TEST_CODE = "test_code";
    private static final String ENROLLMENT_UID = "enrollment_uid";

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

        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityModel.Columns.UID, TRACKED_ENTITY_UID);
        db.insert(TrackedEntityModel.TABLE, null, trackedEntity);

        ContentValues tei = new ContentValues();
        tei.put(TrackedEntityInstanceModel.Columns.UID, TEI_UID);
        tei.put(TrackedEntityInstanceModel.Columns.TRACKED_ENTITY, TRACKED_ENTITY_UID);
        tei.put(TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT, ORGANISATION_UNIT_UID);
        db.insert(TrackedEntityInstanceModel.TABLE, null, tei);

        ContentValues enrollment = new ContentValues();
        enrollment.put(EnrollmentModel.Columns.UID, ENROLLMENT_UID);
        enrollment.put(EnrollmentModel.Columns.PROGRAM, PROGRAM_UID);
        enrollment.put(EnrollmentModel.Columns.ORGANISATION_UNIT, ORGANISATION_UNIT_UID);
        enrollment.put(EnrollmentModel.Columns.TRACKED_ENTITY_INSTANCE, TEI_UID);
        db.insert(EnrollmentModel.TABLE, null, enrollment);

        Date currentDate = BaseIdentifiableObject.DATE_FORMAT.parse(CURRENT_DATE);

        CodeGenerator codeGenerator = () -> TEST_CODE;
        CurrentDateProvider currentDateProvider = () -> currentDate;
        createItemsRepository = new EnrollmentEventRepositoryImpl(databaseRule.briteDatabase(),
                codeGenerator, currentDateProvider, ENROLLMENT_UID);
    }

    @Test
    public void saveMustPersistEventWithCorrectProperties() {
        TestObserver<String> testObserver = createItemsRepository.save(
                ORGANISATION_UNIT_UID, PS_UID).test();

        testObserver.assertNoErrors();
        testObserver.assertComplete();
        testObserver.assertValueCount(1);

        assertThat(testObserver.values().get(0)).isEqualTo(TEST_CODE);

        assertThatCursor(databaseRule.database()
                .query(EventModel.TABLE, EVENT_PROJECTION, null, null, null, null, null))
                .hasRow(TEST_CODE, CURRENT_DATE, CURRENT_DATE, CURRENT_DATE,
                        ENROLLMENT_UID, PROGRAM_UID, PS_UID, ORGANISATION_UNIT_UID,
                        EventStatus.ACTIVE.name(), State.TO_POST.name())
                .isExhausted();
    }
}
