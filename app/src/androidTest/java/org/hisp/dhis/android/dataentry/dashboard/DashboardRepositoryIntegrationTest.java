package org.hisp.dhis.android.dataentry.dashboard;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;

public class DashboardRepositoryIntegrationTest {

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    DashboardRepository dashboardRepository;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();
        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, "org_unit_uid");
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, "program_uid");
        program.put(ProgramModel.Columns.DISPLAY_NAME, "Program");
        db.insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = new ContentValues();
        programStage.put(ProgramStageModel.Columns.UID, "program_stage_uid");
        programStage.put(ProgramStageModel.Columns.DISPLAY_NAME, "Program Stage 1");
        programStage.put(ProgramStageModel.Columns.SORT_ORDER, "1");
        programStage.put(ProgramStageModel.Columns.PROGRAM, "program_uid");
        db.insert(ProgramStageModel.TABLE, null, programStage);

        ContentValues secondStage = new ContentValues();
        secondStage.put(ProgramStageModel.Columns.UID, "program_stage_uid_2");
        secondStage.put(ProgramStageModel.Columns.DISPLAY_NAME, "Program Stage 2");
        secondStage.put(ProgramStageModel.Columns.SORT_ORDER, "2");
        secondStage.put(ProgramStageModel.Columns.PROGRAM, "program_uid");
        db.insert(ProgramStageModel.TABLE, null, secondStage);

        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityModel.Columns.UID, "tracked_entity_uid");
        db.insert(TrackedEntityModel.TABLE, null, trackedEntity);

        ContentValues trackedEntityInstance = new ContentValues();
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.UID, "tei_uid");
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT, "org_unit_uid");
        trackedEntityInstance.put(TrackedEntityInstanceModel.Columns.TRACKED_ENTITY, "tracked_entity_uid");
        db.insert(TrackedEntityInstanceModel.TABLE, null, trackedEntityInstance);

        ContentValues enrollment = new ContentValues();
        enrollment.put(EnrollmentModel.Columns.UID, "enrollment_uid");
        enrollment.put(EnrollmentModel.Columns.DATE_OF_ENROLLMENT, "2016-05-11");
        enrollment.put(EnrollmentModel.Columns.ORGANISATION_UNIT, "org_unit_uid");
        enrollment.put(EnrollmentModel.Columns.PROGRAM, "program_uid");
        enrollment.put(EnrollmentModel.Columns.TRACKED_ENTITY_INSTANCE, "tei_uid");
        db.insert(EnrollmentModel.TABLE, null, enrollment);

        ContentValues event = new ContentValues();
        event.put(EventModel.Columns.UID, "event_uid");
        event.put(EventModel.Columns.PROGRAM, "program_uid");
        event.put(EventModel.Columns.PROGRAM_STAGE, "program_stage_uid");
        event.put(EventModel.Columns.ORGANISATION_UNIT, "org_unit_uid");
        event.put(EventModel.Columns.ENROLLMENT_UID, "enrollment_uid");
        event.put(EventModel.Columns.EVENT_DATE, "2001-12-31");
        event.put(EventModel.Columns.STATUS, EventStatus.ACTIVE.name());
        db.insert(EventModel.TABLE, null, event);

        ContentValues secondEvent = new ContentValues();
        secondEvent.put(EventModel.Columns.UID, "event_uid_2");
        secondEvent.put(EventModel.Columns.PROGRAM, "program_uid");
        secondEvent.put(EventModel.Columns.PROGRAM_STAGE, "program_stage_uid_2");
        secondEvent.put(EventModel.Columns.ORGANISATION_UNIT, "org_unit_uid");
        secondEvent.put(EventModel.Columns.ENROLLMENT_UID, "enrollment_uid");
        secondEvent.put(EventModel.Columns.EVENT_DATE, "1999-12-31");
        secondEvent.put(EventModel.Columns.STATUS, EventStatus.SKIPPED.name());
        db.insert(EventModel.TABLE, null, secondEvent);

        dashboardRepository = new DashboardRepositoryImpl(databaseRule.briteDatabase());

    }

    @Test
    public void attributesShouldPropagateCorrectResults() throws Exception {

        TestSubscriber<List<String>> testObserver = dashboardRepository.attributes("enrollment_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        // no attributes exist yet so list should be empty
        assertThat(testObserver.values().get(0).size()).isEqualTo(0);

        BriteDatabase briteDb = databaseRule.briteDatabase();
        BriteDatabase.Transaction transaction = briteDb.newTransaction();
        // insert one attribute (first name)
        ContentValues firstNameAttribute = new ContentValues();
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.UID, "first_name_attribute_uid");
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_NAME, "First name");
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST_NO_PROGRAM, 1);
        briteDb.insert(TrackedEntityAttributeModel.TABLE, firstNameAttribute);
        // with corresponding value
        ContentValues firstNameValue = new ContentValues();
        firstNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE, "first_name_attribute_uid");
        firstNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE, "tei_uid");
        firstNameValue.put(TrackedEntityAttributeValueModel.Columns.VALUE, "Lucky");
        briteDb.insert(TrackedEntityAttributeValueModel.TABLE, firstNameValue);

        transaction.markSuccessful();
        transaction.end();

        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(1).size()).isEqualTo(1);
        assertThat(testObserver.values().get(1).get(0)).isEqualTo("First name: Lucky");

        BriteDatabase.Transaction newTransaction = briteDb.newTransaction();

        // insert second attribute (last name)
        ContentValues lastNameAttribute = new ContentValues();
        lastNameAttribute.put(TrackedEntityAttributeModel.Columns.UID, "last_name_attribute_uid");
        lastNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_NAME, "Last name");
        lastNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST_NO_PROGRAM, 1);
        briteDb.insert(TrackedEntityAttributeModel.TABLE, lastNameAttribute);
        // with corresponding value
        ContentValues lastNameValue = new ContentValues();
        lastNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE, "last_name_attribute_uid");
        lastNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE, "tei_uid");
        lastNameValue.put(TrackedEntityAttributeValueModel.Columns.VALUE, "Tommy");
        briteDb.insert(TrackedEntityAttributeValueModel.TABLE, lastNameValue);
        newTransaction.markSuccessful();
        newTransaction.end();

        testObserver.assertValueCount(3);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(2).size()).isEqualTo(2);
        assertThat(testObserver.values().get(2).get(0)).isEqualTo("First name: Lucky");
        assertThat(testObserver.values().get(2).get(1)).isEqualTo("Last name: Tommy");
    }

    @Test
    public void attributesNotMarkedAsDisplayInListShouldNotBePropagated() throws Exception {
        TestSubscriber<List<String>> testObserver = dashboardRepository.attributes("enrollment_uid").test();

        BriteDatabase briteDb = databaseRule.briteDatabase();
        BriteDatabase.Transaction transaction = briteDb.newTransaction();

        ContentValues firstNameAttribute = new ContentValues();
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.UID, "first_name_attribute_uid");
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_NAME, "First name");
        // not marked as display in list
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST_NO_PROGRAM, 0);
        briteDb.insert(TrackedEntityAttributeModel.TABLE, firstNameAttribute);

        ContentValues firstNameValue = new ContentValues();
        firstNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE, "first_name_attribute_uid");
        firstNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE, "tei_uid");
        firstNameValue.put(TrackedEntityAttributeValueModel.Columns.VALUE, "Lucky");
        briteDb.insert(TrackedEntityAttributeValueModel.TABLE, firstNameValue);

        transaction.markSuccessful();
        transaction.end();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        // no attributes marked as display in list so list should be empty
        assertThat(testObserver.values().get(0).size()).isEqualTo(0);
    }

    @Test
    public void attributeSortOrderShouldBeRespected() throws Exception {
        BriteDatabase briteDb = databaseRule.briteDatabase();
        BriteDatabase.Transaction transaction = briteDb.newTransaction();

        ContentValues firstNameAttribute = new ContentValues();
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.UID, "first_name_attribute_uid");
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_NAME, "First name");
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST_NO_PROGRAM, 1);
        firstNameAttribute.put(TrackedEntityAttributeModel.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM, 2);
        briteDb.insert(TrackedEntityAttributeModel.TABLE, firstNameAttribute);

        ContentValues firstNameValue = new ContentValues();
        firstNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE, "first_name_attribute_uid");
        firstNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE, "tei_uid");
        firstNameValue.put(TrackedEntityAttributeValueModel.Columns.VALUE, "Lucky");
        briteDb.insert(TrackedEntityAttributeValueModel.TABLE, firstNameValue);

        ContentValues lastNameAttribute = new ContentValues();
        lastNameAttribute.put(TrackedEntityAttributeModel.Columns.UID, "last_name_attribute_uid");
        lastNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_NAME, "Last name");
        lastNameAttribute.put(TrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST_NO_PROGRAM, 1);
        lastNameAttribute.put(TrackedEntityAttributeModel.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM, 1);
        briteDb.insert(TrackedEntityAttributeModel.TABLE, lastNameAttribute);

        ContentValues lastNameValue = new ContentValues();
        lastNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE, "last_name_attribute_uid");
        lastNameValue.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE, "tei_uid");
        lastNameValue.put(TrackedEntityAttributeValueModel.Columns.VALUE, "Tommy");
        briteDb.insert(TrackedEntityAttributeValueModel.TABLE, lastNameValue);

        transaction.markSuccessful();
        transaction.end();

        TestSubscriber<List<String>> testObserver = dashboardRepository.attributes("enrollment_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0).size()).isEqualTo(2);
        // last name comes first as it has sort order 1
        assertThat(testObserver.values().get(0).get(0)).isEqualTo("Last name: Tommy");
        // first name has sort order 2
        assertThat(testObserver.values().get(0).get(1)).isEqualTo("First name: Lucky");
    }

    @Test
    public void eventsShouldPropagateCorrectResults() throws Exception {
        EventViewModel firstEvent =
                EventViewModel.create("event_uid", "Program Stage 1", "2001-12-31", EventStatus.ACTIVE);
        EventViewModel secondEvent =
                EventViewModel.create("event_uid_2", "Program Stage 2", "1999-12-31", EventStatus.SKIPPED);

        TestSubscriber<List<EventViewModel>> testObserver = dashboardRepository.events("enrollment_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0).size()).isEqualTo(2);
        assertThat(testObserver.values().get(0).get(0)).isEqualTo(firstEvent);
        assertThat(testObserver.values().get(0).get(1)).isEqualTo(secondEvent);

        BriteDatabase briteDb = databaseRule.briteDatabase();
        BriteDatabase.Transaction transaction = briteDb.newTransaction();

        ContentValues firstEventValues = new ContentValues();
        firstEventValues.put(EventModel.Columns.EVENT_DATE, "2099-02-01");
        databaseRule.briteDatabase().update(EventModel.TABLE, firstEventValues, EventModel.Columns.UID + " = 'event_uid'");

        ContentValues secondEventValues = new ContentValues();
        secondEventValues.put(EventModel.Columns.EVENT_DATE, "2199-02-01");
        databaseRule.briteDatabase().update(EventModel.TABLE, secondEventValues, EventModel.Columns.UID + " = 'event_uid_2'");

        transaction.markSuccessful();
        transaction.end();

        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        assertThat(testObserver.values().get(1).size()).isEqualTo(2);
        // Events have changed places (secondEvent comes first in the list), as they are sorted descending by date
        assertThat(testObserver.values().get(1).get(0).date()).isEqualTo("2199-02-01");
        assertThat(testObserver.values().get(1).get(1).date()).isEqualTo("2099-02-01");
    }
}