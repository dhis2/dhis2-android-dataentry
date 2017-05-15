package org.hisp.dhis.android.dataentry.form;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class EnrollmentFormRepositoryIntegrationTest {

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

        formRepository = new EnrollmentFormRepository(databaseRule.briteDatabase());
    }

    @Test
    public void titleShouldPropagateCorrectResults() throws Exception {

        TestSubscriber<String> testObserver =
                formRepository.title("enrollment_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0)).isEqualTo("Program");

        ContentValues programWithNewName = new ContentValues();
        programWithNewName.put(ProgramModel.Columns.DISPLAY_NAME, "New Program Name");
        databaseRule.briteDatabase().update(
                ProgramModel.TABLE, programWithNewName, "Program.uid = 'program_uid'", null);
        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(1)).isEqualTo("New Program Name");

    }

    @Test
    public void reportDateShouldPropagateCorrectResults() throws Exception {

        TestSubscriber<String> testObserver =
                formRepository.reportDate("enrollment_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0)).isEqualTo("2016-05-11");

        ContentValues enrollmentWithNewDate = new ContentValues();
        enrollmentWithNewDate.put(EnrollmentModel.Columns.DATE_OF_ENROLLMENT, "2099-05-01");
        databaseRule.briteDatabase()
                .update(EnrollmentModel.TABLE, enrollmentWithNewDate, "Enrollment.uid = 'enrollment_uid'", null);
        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(1)).isEqualTo("2099-05-01");
    }

    @Test
    public void sectionsShouldPropagateCorrectResults() throws Exception {

        FormSectionViewModel formSectionViewModel =
                FormSectionViewModel.createForEnrollment("enrollment_uid");

        TestSubscriber<List<FormSectionViewModel>> testObserver = formRepository.sections("enrollment_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0).size()).isEqualTo(1);
        assertThat(testObserver.values().get(0).get(0)).isEqualTo(formSectionViewModel);
    }

    @Test
    public void reportDateShouldBeStoredCorrectly() throws Exception {

        formRepository.storeReportDate("enrollment_uid").accept("2019-09-09");

        Cursor cursor = db.rawQuery(
                "SELECT Enrollment.enrollmentDate FROM Enrollment WHERE Enrollment.uid = 'enrollment_uid'", null);
        cursor.moveToFirst();
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(cursor.getString(0)).isEqualTo("2019-09-09");
        cursor.close();
    }

}