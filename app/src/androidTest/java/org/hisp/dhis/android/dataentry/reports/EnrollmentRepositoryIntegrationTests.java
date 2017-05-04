package org.hisp.dhis.android.dataentry.reports;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class EnrollmentRepositoryIntegrationTests {

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private ReportsRepository reportsRepository;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = databaseRule.database();

        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, "organization_unit_uid");
        db.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues trackedEntity = new ContentValues();
        trackedEntity.put(TrackedEntityModel.Columns.UID, "tracked_entity_uid");
        db.insert(TrackedEntityModel.TABLE, null, trackedEntity);

        // Programs
        db.insert(ProgramModel.TABLE, null, program("program_uid_one",
                null, null, "tracked_entity_uid"));
        db.insert(ProgramModel.TABLE, null, program("program_uid_two",
                "program_display_name_two", "program_enrollment_date_label_two", "tracked_entity_uid"));
        db.insert(ProgramModel.TABLE, null, program("program_uid_three",
                "program_display_name_three", "program_enrollment_date_label_three", "tracked_entity_uid"));

        // TrackedEntityAttributes
        db.insert(TrackedEntityAttributeModel.TABLE, null, tea("tea_uid_one", "tea_one"));
        db.insert(TrackedEntityAttributeModel.TABLE, null, tea("tea_uid_two", "tea_two"));

        // ProgramTrackedEntityAttributes
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_one", "program_uid_one", "tea_uid_one", false, 2));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_two", "program_uid_one", "tea_uid_two", false, 1));

        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_three", "program_uid_two", "tea_uid_one", true, 2));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_four", "program_uid_two", "tea_uid_two", true, 1));

        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_five", "program_uid_three", "tea_uid_one", true, 2));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_six", "program_uid_three", "tea_uid_two", true, 1));

        // TrackedEntityInstances
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid", BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.SYNCED));

        // TrackedEntityAttributeValues
        db.insert(TrackedEntityAttributeValueModel.TABLE, null,
                teav("tei_uid", "tea_uid_one", "teav_one"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null,
                teav("tei_uid", "tea_uid_two", "teav_two"));

        reportsRepository = new EnrollmentsRepositoryImpl(databaseRule.briteDatabase(),
                "program", "enrollment_status", "enrollment_date_label");
    }

    @Test
    public void reportsShouldPropagateCorrectResults() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        // Enrollments
        db.insert(EnrollmentModel.TABLE, null, enrollment("enrollment_uid_one",
                "program_uid_one", "organization_unit_uid", "tei_uid",
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-04-06T00:05:57.495"), "2014-05-01",
                State.SYNCED, EnrollmentStatus.ACTIVE));
        db.insert(EnrollmentModel.TABLE, null, enrollment("enrollment_uid_two",
                "program_uid_two", "organization_unit_uid", "tei_uid",
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-06-06T00:05:57.495"), "2016-10-01",
                State.TO_UPDATE, EnrollmentStatus.COMPLETED));
        db.insert(EnrollmentModel.TABLE, null, enrollment("enrollment_uid_three",
                "program_uid_three", "organization_unit_uid", "tei_uid",
                BaseIdentifiableObject.DATE_FORMAT.parse("2011-06-06T00:05:57.495"), "2011-10-01",
                State.TO_DELETE, EnrollmentStatus.CANCELLED));

        // ReportView models
        ReportViewModel reportViewModelTwo = ReportViewModel.create("enrollment_uid_two",
                ReportViewModel.Status.TO_SYNC, Arrays.asList("program: program_display_name_two",
                        "program_enrollment_date_label_two: 2016-10-01", "tea_two: teav_two",
                        "tea_one: teav_one", "enrollment_status: COMPLETED"));
        ReportViewModel reportViewModelOne = ReportViewModel.create("enrollment_uid_one",
                ReportViewModel.Status.SYNCED, Arrays.asList("program: -",
                        "enrollment_date_label: 2014-05-01", "", "enrollment_status: ACTIVE"));

        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tei_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);

        assertThat(reports.size()).isEqualTo(2);
        assertThat(reports.get(0)).isEqualTo(reportViewModelTwo);
        assertThat(reports.get(1)).isEqualTo(reportViewModelOne);
    }

    @Test
    public void reportsShouldObserveChangesInDatabase() throws ParseException {
        SQLiteDatabase db = databaseRule.database();
        BriteDatabase briteDatabase = databaseRule.briteDatabase();

        // Enrollments
        db.insert(EnrollmentModel.TABLE, null, enrollment("enrollment_uid_one",
                "program_uid_one", "organization_unit_uid", "tei_uid",
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-04-06T00:05:57.495"), "2014-05-01",
                State.SYNCED, EnrollmentStatus.ACTIVE));
        ReportViewModel reportViewModelOne = ReportViewModel.create("enrollment_uid_one",
                ReportViewModel.Status.SYNCED, Arrays.asList("program: -",
                        "enrollment_date_label: 2014-05-01", "", "enrollment_status: ACTIVE"));

        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tei_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        assertThat(testObserver.values().get(0).size()).isEqualTo(1);
        assertThat(testObserver.values().get(0).get(0)).isEqualTo(reportViewModelOne);

        // insert new row
        briteDatabase.insert(EnrollmentModel.TABLE, enrollment("enrollment_uid_two",
                "program_uid_two", "organization_unit_uid", "tei_uid",
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-06-06T00:05:57.495"), "2016-10-01",
                State.TO_UPDATE, EnrollmentStatus.COMPLETED));
        ReportViewModel reportViewModelTwo = ReportViewModel.create("enrollment_uid_two",
                ReportViewModel.Status.TO_SYNC, Arrays.asList("program: program_display_name_two",
                        "program_enrollment_date_label_two: 2016-10-01", "tea_two: teav_two",
                        "tea_one: teav_one", "enrollment_status: COMPLETED"));

        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        assertThat(testObserver.values().get(1).size()).isEqualTo(2);
        assertThat(testObserver.values().get(1).get(0)).isEqualTo(reportViewModelTwo);
        assertThat(testObserver.values().get(1).get(1)).isEqualTo(reportViewModelOne);

        // mark row as deleted
        ContentValues updatedEnrollment = new ContentValues();
        updatedEnrollment.put(EnrollmentModel.Columns.STATE, State.TO_DELETE.toString());
        briteDatabase.update(EnrollmentModel.TABLE, updatedEnrollment,
                EnrollmentModel.Columns.UID + " = ?", "enrollment_uid_one");

        testObserver.assertValueCount(3);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        assertThat(testObserver.values().get(2).size()).isEqualTo(1);
        assertThat(testObserver.values().get(2).get(0)).isEqualTo(reportViewModelTwo);
    }

    @Test
    public void reportsWithoutVisiblePteaMustBePropagated() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        // insert enrollment which doesn't have any visible PTEAs
        db.insert(EnrollmentModel.TABLE, null, enrollment("enrollment_uid_one",
                "program_uid_one", "organization_unit_uid", "tei_uid",
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-04-06T00:05:57.495"), "2014-05-01",
                State.SYNCED, EnrollmentStatus.ACTIVE));
        ReportViewModel reportViewModelOne = ReportViewModel.create("enrollment_uid_one",
                ReportViewModel.Status.SYNCED, Arrays.asList("program: -",
                        "enrollment_date_label: 2014-05-01", "", "enrollment_status: ACTIVE"));

        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tei_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);

        assertThat(reports.size()).isEqualTo(1);
        assertThat(reports.get(0)).isEqualTo(reportViewModelOne);
    }

    @Test
    public void reportsWithoutTeavsShouldBePropagated() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        // remove all TEAVs
        db.delete(TrackedEntityAttributeValueModel.TABLE, null, null);

        db.insert(EnrollmentModel.TABLE, null, enrollment("enrollment_uid_two",
                "program_uid_two", "organization_unit_uid", "tei_uid",
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-06-06T00:05:57.495"), "2016-10-01",
                State.TO_UPDATE, EnrollmentStatus.COMPLETED));
        ReportViewModel reportViewModelTwo = ReportViewModel.create("enrollment_uid_two",
                ReportViewModel.Status.TO_SYNC, Arrays.asList("program: program_display_name_two",
                        "program_enrollment_date_label_two: 2016-10-01", "tea_two: -",
                        "tea_one: -", "enrollment_status: COMPLETED"));

        // insert enrollment which doesn't have any visible PTEAs
        TestSubscriber<List<ReportViewModel>> testObserver
                = reportsRepository.reports("tei_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);

        assertThat(reports.size()).isEqualTo(1);
        assertThat(reports.get(0)).isEqualTo(reportViewModelTwo);
    }

    @Test
    public void reportsWithoutVisiblePteaAndTeavsShouldBePropagated() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        // remove all TEAVs
        db.delete(TrackedEntityAttributeValueModel.TABLE, null, null);

        db.insert(EnrollmentModel.TABLE, null, enrollment("enrollment_uid_one",
                "program_uid_one", "organization_unit_uid", "tei_uid",
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-04-06T00:05:57.495"), "2014-05-01",
                State.SYNCED, EnrollmentStatus.ACTIVE));
        ReportViewModel reportViewModelOne = ReportViewModel.create("enrollment_uid_one",
                ReportViewModel.Status.SYNCED, Arrays.asList("program: -",
                        "enrollment_date_label: 2014-05-01", "", "enrollment_status: ACTIVE"));

        // insert enrollment which doesn't have any visible PTEAs
        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tei_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);

        assertThat(reports.size()).isEqualTo(1);
        assertThat(reports.get(0)).isEqualTo(reportViewModelOne);
    }

    @Test
    public void reportsWithoutAnyDataShouldPropagateEmptyList() {
        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tei_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);
        assertThat(reports.size()).isEqualTo(0);
    }

    @NonNull
    private static ContentValues program(@NonNull String uid, @Nullable String displayName,
            @Nullable String enrollmentDateLabel, @NonNull String trackedEntity) {
        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, uid);
        program.put(ProgramModel.Columns.DISPLAY_NAME, displayName);
        program.put(ProgramModel.Columns.ENROLLMENT_DATE_LABEL, enrollmentDateLabel);
        program.put(ProgramModel.Columns.TRACKED_ENTITY, trackedEntity);
        return program;
    }

    @NonNull
    private static ContentValues enrollment(@NonNull String uid, @NonNull String program, @NonNull String orgUnit,
            @NonNull String tei, @NonNull Date created, @NonNull String date, @NonNull State state,
            @NonNull EnrollmentStatus status) {
        ContentValues enrollment = new ContentValues();
        enrollment.put(EnrollmentModel.Columns.UID, uid);
        enrollment.put(EnrollmentModel.Columns.PROGRAM, program);
        enrollment.put(EnrollmentModel.Columns.ORGANISATION_UNIT, orgUnit);
        enrollment.put(EnrollmentModel.Columns.TRACKED_ENTITY_INSTANCE, tei);
        enrollment.put(EnrollmentModel.Columns.CREATED,
                BaseIdentifiableObject.DATE_FORMAT.format(created));
        enrollment.put(EnrollmentModel.Columns.DATE_OF_ENROLLMENT, date);
        enrollment.put(EnrollmentModel.Columns.STATE, state.toString());
        enrollment.put(EnrollmentModel.Columns.ENROLLMENT_STATUS, status.toString());
        return enrollment;
    }

    @NonNull
    private static ContentValues tei(@NonNull String uid, @Nonnull Date created,
            @NonNull String orgUnit, @NonNull String trackedEntity, @NonNull State state) {
        ContentValues tei = new ContentValues();
        tei.put(TrackedEntityInstanceModel.Columns.UID, uid);
        tei.put(TrackedEntityInstanceModel.Columns.CREATED,
                BaseIdentifiableObject.DATE_FORMAT.format(created));
        tei.put(TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT, orgUnit);
        tei.put(TrackedEntityInstanceModel.Columns.TRACKED_ENTITY, trackedEntity);
        tei.put(TrackedEntityInstanceModel.Columns.STATE, state.toString());
        return tei;
    }

    @NonNull
    private static ContentValues ptea(@NonNull String uid, @NonNull String program,
            @NonNull String trackedEntityAttribute, boolean displayInList, int sortOrder) {
        ContentValues ptea = new ContentValues();
        ptea.put(ProgramTrackedEntityAttributeModel.Columns.UID, uid);
        ptea.put(ProgramTrackedEntityAttributeModel.Columns.PROGRAM, program);
        ptea.put(ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE, trackedEntityAttribute);
        ptea.put(ProgramTrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST, displayInList ? 1 : 0);
        ptea.put(ProgramTrackedEntityAttributeModel.Columns.SORT_ORDER, sortOrder);
        return ptea;
    }

    @NonNull
    private static ContentValues tea(@NonNull String uid, @NonNull String displayName) {
        ContentValues tea = new ContentValues();
        tea.put(TrackedEntityAttributeModel.Columns.UID, uid);
        tea.put(TrackedEntityAttributeModel.Columns.DISPLAY_NAME, displayName);
        return tea;
    }

    @NonNull
    private static ContentValues teav(@NonNull String tei, @NonNull String tea, @NonNull String value) {
        ContentValues teav = new ContentValues();
        teav.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE, tei);
        teav.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE, tea);
        teav.put(TrackedEntityAttributeValueModel.Columns.VALUE, value);
        return teav;
    }
}
