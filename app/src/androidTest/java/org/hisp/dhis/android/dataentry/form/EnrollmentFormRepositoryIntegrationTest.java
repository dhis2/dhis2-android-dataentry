package org.hisp.dhis.android.dataentry.form;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.duktape.Duktape;

import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.dataentry.commons.utils.CodeGenerator;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.hisp.dhis.rules.android.DuktapeEvaluator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class EnrollmentFormRepositoryIntegrationTest {

    @Mock
    private CodeGenerator codeGenerator;

    @Mock
    private CurrentDateProvider currentDateProvider;

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private FormRepository formRepository;
    private Duktape duktape;

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

        initMocks(this);

        duktape = Duktape.create();

        formRepository = new EnrollmentFormRepository(databaseRule.briteDatabase(),
                new DuktapeEvaluator(duktape), new RulesRepository(databaseRule.briteDatabase()),
                codeGenerator, currentDateProvider, "enrollment_uid");
    }

    @After
    public void tearDown() throws Exception {
        duktape.close();
    }

    @Test
    public void titleShouldPropagateCorrectResults() throws Exception {
        TestSubscriber<String> testObserver = formRepository.title().test();

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
        TestSubscriber<String> testObserver = formRepository.reportDate().test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0)).isEqualTo("2016-05-11");

        ContentValues enrollmentWithNewDate = new ContentValues();
        enrollmentWithNewDate.put(EnrollmentModel.Columns.DATE_OF_ENROLLMENT, "2099-05-01");
        databaseRule.briteDatabase().update(EnrollmentModel.TABLE,
                enrollmentWithNewDate, "Enrollment.uid = 'enrollment_uid'", null);

        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(1)).isEqualTo("2099-05-01");
    }

    @Test
    public void reportStatusShouldPropagateCorrectResults() throws Exception {
        ContentValues activeEnrollment = new ContentValues();
        activeEnrollment.put(EnrollmentModel.Columns.ENROLLMENT_STATUS, EnrollmentStatus.ACTIVE.name());
        databaseRule.briteDatabase()
                .update(EnrollmentModel.TABLE, activeEnrollment, "Enrollment.uid = 'enrollment_uid'", null);

        TestSubscriber<ReportStatus> testObserver = formRepository.reportStatus().test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0)).isEqualTo(ReportStatus.ACTIVE);

        ContentValues completedEnrollment = new ContentValues();
        completedEnrollment.put(EnrollmentModel.Columns.ENROLLMENT_STATUS, EnrollmentStatus.COMPLETED.name());
        databaseRule.briteDatabase()
                .update(EnrollmentModel.TABLE, completedEnrollment, "Enrollment.uid = 'enrollment_uid'", null);
        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(1)).isEqualTo(ReportStatus.COMPLETED);
    }

    @Test
    public void sectionsShouldPropagateCorrectResults() throws Exception {
        FormSectionViewModel formSectionViewModel =
                FormSectionViewModel.createForEnrollment("enrollment_uid");

        TestSubscriber<List<FormSectionViewModel>> testObserver =
                formRepository.sections().test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();
        assertThat(testObserver.values().get(0).size()).isEqualTo(1);
        assertThat(testObserver.values().get(0).get(0)).isEqualTo(formSectionViewModel);
    }

    @Test
    public void reportDateShouldBeStoredCorrectly() throws Exception {
        formRepository.storeReportDate().accept("2019-09-09");

        Cursor cursor = databaseRule.database().rawQuery("SELECT Enrollment.enrollmentDate FROM " +
                "Enrollment WHERE Enrollment.uid = 'enrollment_uid'", null);
        cursor.moveToFirst();
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(cursor.getString(0)).isEqualTo("2019-09-09");
        cursor.close();
    }

    @Test
    public void enrollmentStatusShouldBeStoredCorrectly() throws Exception {
        formRepository.storeReportStatus().accept(ReportStatus.COMPLETED);

        Cursor cursor = databaseRule.database().rawQuery("SELECT Enrollment.status FROM " +
                "Enrollment WHERE Enrollment.uid = 'enrollment_uid'", null);
        cursor.moveToFirst();
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(cursor.getString(0)).isEqualTo(EnrollmentStatus.COMPLETED.name());
        cursor.close();
    }
}