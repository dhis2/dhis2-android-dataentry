package org.hisp.dhis.android.dataentry.reports;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
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
public class TeisRepositoryIntegrationTests {

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

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, "program_uid");
        program.put(ProgramModel.Columns.TRACKED_ENTITY, "tracked_entity_uid");
        db.insert(ProgramModel.TABLE, null, program);

        db.insert(TrackedEntityAttributeModel.TABLE, null, tea("tea_uid_one", "tea_one"));
        db.insert(TrackedEntityAttributeModel.TABLE, null, tea("tea_uid_two", "tea_two"));
        db.insert(TrackedEntityAttributeModel.TABLE, null, tea("tea_uid_three", "tea_three"));

        reportsRepository = new TeisRepositoryImpl(databaseRule.briteDatabase());
    }

    @Test
    public void reportsShouldPropagateCorrectResults() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        // ProgramTrackedEntityAttributes
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_one", "program_uid", "tea_uid_one", true, 2));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_two", "program_uid", "tea_uid_two", true, 1));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_three", "program_uid", "tea_uid_three", false, 0));

        // TrackedEntityInstances
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_one", BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.SYNCED));
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_two", BaseIdentifiableObject.DATE_FORMAT.parse("2017-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.TO_UPDATE));
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_three", BaseIdentifiableObject.DATE_FORMAT.parse("2013-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.TO_DELETE));

        // TrackedEntityAttributeValues
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_one", "tea_uid_one", "teav_one"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_one", "tea_uid_two", "teav_two"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_one", "tea_uid_three", "teav_three"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_two", "tea_uid_one", "teav_four"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_two", "tea_uid_two", "teav_five"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_two", "tea_uid_three", "teav_six"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_three", "tea_uid_one", "teav_seven"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_three", "tea_uid_two", "teav_right"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_three", "tea_uid_three", "teav_nine"));

        ReportViewModel reportViewModelTwo = ReportViewModel.create("tei_uid_two",
                ReportViewModel.Status.TO_SYNC, Arrays.asList("tea_two: teav_five", "tea_one: teav_four"));
        ReportViewModel reportViewModelOne = ReportViewModel.create("tei_uid_one",
                ReportViewModel.Status.SYNCED, Arrays.asList("tea_two: teav_two", "tea_one: teav_one"));

        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tracked_entity_uid").test();

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

        // ProgramTrackedEntityAttributes
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_one", "program_uid", "tea_uid_one", true, 2));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_two", "program_uid", "tea_uid_two", true, 1));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_three", "program_uid", "tea_uid_three", false, 0));

        // TrackedEntityInstance - One
        BriteDatabase.Transaction transaction = briteDatabase.newTransaction();
        briteDatabase.insert(TrackedEntityInstanceModel.TABLE, tei("tei_uid_one",
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                "organization_unit_uid", "tracked_entity_uid", State.SYNCED));
        briteDatabase.insert(TrackedEntityAttributeValueModel.TABLE, teav("tei_uid_one", "tea_uid_one", "teav_one"));
        briteDatabase.insert(TrackedEntityAttributeValueModel.TABLE, teav("tei_uid_one", "tea_uid_two", "teav_two"));
        briteDatabase.insert(TrackedEntityAttributeValueModel.TABLE, teav("tei_uid_one", "tea_uid_three", "teav_three"));
        ReportViewModel reportViewModelOne = ReportViewModel.create("tei_uid_one",
                ReportViewModel.Status.SYNCED, Arrays.asList("tea_two: teav_two", "tea_one: teav_one"));
        transaction.markSuccessful();
        transaction.close();

        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tracked_entity_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        assertThat(testObserver.values().get(0).size()).isEqualTo(1);
        assertThat(testObserver.values().get(0).get(0)).isEqualTo(reportViewModelOne);

        // TrackedEntityInstance - Two
        transaction = briteDatabase.newTransaction();
        briteDatabase.insert(TrackedEntityInstanceModel.TABLE, tei("tei_uid_two",
                BaseIdentifiableObject.DATE_FORMAT.parse("2017-04-06T00:05:57.495"),
                "organization_unit_uid", "tracked_entity_uid", State.TO_POST));
        briteDatabase.insert(TrackedEntityAttributeValueModel.TABLE, teav("tei_uid_two", "tea_uid_one", "teav_four"));
        briteDatabase.insert(TrackedEntityAttributeValueModel.TABLE, teav("tei_uid_two", "tea_uid_two", "teav_five"));
        briteDatabase.insert(TrackedEntityAttributeValueModel.TABLE, teav("tei_uid_two", "tea_uid_three", "teav_six"));
        ReportViewModel reportViewModelTwo = ReportViewModel.create("tei_uid_two",
                ReportViewModel.Status.TO_SYNC, Arrays.asList("tea_two: teav_five", "tea_one: teav_four"));
        transaction.markSuccessful();
        transaction.close();

        testObserver.assertValueCount(2);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        assertThat(testObserver.values().get(1).size()).isEqualTo(2);
        assertThat(testObserver.values().get(1).get(0)).isEqualTo(reportViewModelTwo);
        assertThat(testObserver.values().get(1).get(1)).isEqualTo(reportViewModelOne);

        // TrackedEntityInstance - One
        ContentValues updatedState = new ContentValues();
        updatedState.put(TrackedEntityInstanceModel.Columns.STATE, State.TO_DELETE.toString());
        briteDatabase.update(TrackedEntityInstanceModel.TABLE, updatedState,
                TrackedEntityInstanceModel.Columns.UID + " = ?", "tei_uid_one");

        testObserver.assertValueCount(3);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        assertThat(testObserver.values().get(2).size()).isEqualTo(1);
        assertThat(testObserver.values().get(2).get(0)).isEqualTo(reportViewModelTwo);
    }

    @Test
    public void reportsWithoutAnyDataShouldPropagateEmptyList() {
        SQLiteDatabase db = databaseRule.database();

        // ProgramTrackedEntityAttributes
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_one", "program_uid", "tea_uid_one", false, 2));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_two", "program_uid", "tea_uid_two", false, 1));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_three", "program_uid", "tea_uid_three", false, 0));

        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tracked_entity_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);
        assertThat(reports.size()).isEqualTo(0);
    }

    @Test
    public void reportsWithoutVisiblePteaMustBePropagated() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        // ProgramTrackedEntityAttributes
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_one", "program_uid", "tea_uid_one", false, 2));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_two", "program_uid", "tea_uid_two", false, 1));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_three", "program_uid", "tea_uid_three", false, 0));

        // TrackedEntityInstances
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_one", BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.SYNCED));
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_two", BaseIdentifiableObject.DATE_FORMAT.parse("2017-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.TO_UPDATE));
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_three", BaseIdentifiableObject.DATE_FORMAT.parse("2013-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.TO_DELETE));

        // TrackedEntityAttributeValues
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_one", "tea_uid_one", "teav_one"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_one", "tea_uid_two", "teav_two"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_one", "tea_uid_three", "teav_three"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_two", "tea_uid_one", "teav_four"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_two", "tea_uid_two", "teav_five"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_two", "tea_uid_three", "teav_six"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_three", "tea_uid_one", "teav_seven"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_three", "tea_uid_two", "teav_right"));
        db.insert(TrackedEntityAttributeValueModel.TABLE, null, teav("tei_uid_three", "tea_uid_three", "teav_nine"));

        ReportViewModel reportViewModelTwo = ReportViewModel.create("tei_uid_two",
                ReportViewModel.Status.TO_SYNC, Arrays.asList(""));
        ReportViewModel reportViewModelOne = ReportViewModel.create("tei_uid_one",
                ReportViewModel.Status.SYNCED, Arrays.asList(""));

        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tracked_entity_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);

        assertThat(reports.size()).isEqualTo(2);
        assertThat(reports.get(0)).isEqualTo(reportViewModelTwo);
        assertThat(reports.get(1)).isEqualTo(reportViewModelOne);
    }

    @Test
    public void reportsWithoutTeavsShouldBePropagated() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        // ProgramTrackedEntityAttributes
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_one", "program_uid", "tea_uid_one", true, 2));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_two", "program_uid", "tea_uid_two", true, 1));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_three", "program_uid", "tea_uid_three", false, 0));

        // TrackedEntityInstances
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_one", BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.SYNCED));
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_two", BaseIdentifiableObject.DATE_FORMAT.parse("2017-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.TO_UPDATE));
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_three", BaseIdentifiableObject.DATE_FORMAT.parse("2013-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.TO_DELETE));

        ReportViewModel reportViewModelTwo = ReportViewModel.create("tei_uid_two",
                ReportViewModel.Status.TO_SYNC, Arrays.asList("tea_two: -", "tea_one: -"));
        ReportViewModel reportViewModelOne = ReportViewModel.create("tei_uid_one",
                ReportViewModel.Status.SYNCED, Arrays.asList("tea_two: -", "tea_one: -"));

        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tracked_entity_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);

        assertThat(reports.size()).isEqualTo(2);
        assertThat(reports.get(0)).isEqualTo(reportViewModelTwo);
        assertThat(reports.get(1)).isEqualTo(reportViewModelOne);
    }

    @Test
    public void reportsWithoutVisiblePteaAndTeavsShouldBePropagated() throws ParseException {
        SQLiteDatabase db = databaseRule.database();

        // ProgramTrackedEntityAttributes
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_one", "program_uid", "tea_uid_one", false, 2));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_two", "program_uid", "tea_uid_two", false, 1));
        db.insert(ProgramTrackedEntityAttributeModel.TABLE, null,
                ptea("ptea_uid_three", "program_uid", "tea_uid_three", false, 0));

        // TrackedEntityInstances
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_one", BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.SYNCED));
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_two", BaseIdentifiableObject.DATE_FORMAT.parse("2017-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.TO_UPDATE));
        db.insert(TrackedEntityInstanceModel.TABLE, null,
                tei("tei_uid_three", BaseIdentifiableObject.DATE_FORMAT.parse("2013-04-06T00:05:57.495"),
                        "organization_unit_uid", "tracked_entity_uid", State.TO_DELETE));

        ReportViewModel reportViewModelTwo = ReportViewModel.create("tei_uid_two",
                ReportViewModel.Status.TO_SYNC, Arrays.asList(""));
        ReportViewModel reportViewModelOne = ReportViewModel.create("tei_uid_one",
                ReportViewModel.Status.SYNCED, Arrays.asList(""));

        TestSubscriber<List<ReportViewModel>> testObserver =
                reportsRepository.reports("tracked_entity_uid").test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);

        assertThat(reports.size()).isEqualTo(2);
        assertThat(reports.get(0)).isEqualTo(reportViewModelTwo);
        assertThat(reports.get(1)).isEqualTo(reportViewModelOne);
    }

    @NonNull
    private static ContentValues tea(@NonNull String uid, @NonNull String displayName) {
        ContentValues tea = new ContentValues();
        tea.put(TrackedEntityAttributeModel.Columns.UID, uid);
        tea.put(TrackedEntityAttributeModel.Columns.DISPLAY_NAME, displayName);
        return tea;
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
    private static ContentValues teav(@NonNull String tei, @NonNull String tea, @NonNull String value) {
        ContentValues teav = new ContentValues();
        teav.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE, tei);
        teav.put(TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE, tea);
        teav.put(TrackedEntityAttributeValueModel.Columns.VALUE, value);
        return teav;
    }
}