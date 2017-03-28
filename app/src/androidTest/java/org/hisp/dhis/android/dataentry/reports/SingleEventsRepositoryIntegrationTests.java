package org.hisp.dhis.android.dataentry.reports;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.reactivex.subscribers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class SingleEventsRepositoryIntegrationTests {

    @Rule
    public DatabaseRule databaseRule = new DatabaseRule(Schedulers.trampoline());

    private ReportsRepository reportsRepository;

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

        db.insert(DataElementModel.TABLE, null,
                dataElement("data_element_one_uid", "data_element_one_name"));
        db.insert(DataElementModel.TABLE, null,
                dataElement("data_element_two_uid", "data_element_two_name"));
        db.insert(DataElementModel.TABLE, null,
                dataElement("data_element_three_uid", "data_element_three_name"));

        db.insert(EventModel.TABLE, null, event("event_one",
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                "organisation_unit_uid", "program_uid", "ps_uid", State.TO_POST));
        db.insert(EventModel.TABLE, null, event("event_two",
                BaseIdentifiableObject.DATE_FORMAT.parse("2017-04-06T00:05:57.495"),
                "organisation_unit_uid", "program_uid", "ps_uid", State.SYNCED));

        reportsRepository = new SingleEventsRepositoryImpl(databaseRule.briteDatabase(), "program_uid");
    }

    @Test
    public void reportsShouldPropagateCorrectResults() throws InterruptedException {
        SQLiteDatabase db = databaseRule.database();
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_one", "ps_uid", "data_element_one_uid", true));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_two", "ps_uid", "data_element_two_uid", true));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_three", "ps_uid", "data_element_three_uid", false));

        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_one_uid", "data_value_one"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_two_uid", "data_value_two"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_three_uid", "data_value_three"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_one_uid", "data_value_four"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_two_uid", "data_value_five"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_three_uid", "data_value_six"));

        ReportViewModel reportViewModelOne = ReportViewModel.create("event_one",
                ReportViewModel.Status.TO_SYNC, Arrays.asList("data_element_one_name: data_value_one",
                        "data_element_two_name: data_value_two"));
        ReportViewModel reportViewModelTwo = ReportViewModel.create("event_two",
                ReportViewModel.Status.SYNCED, Arrays.asList("data_element_one_name: data_value_four",
                        "data_element_two_name: data_value_five"));

        TestSubscriber<List<ReportViewModel>> testObserver = reportsRepository.reports().test();

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
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_one", "ps_uid", "data_element_one_uid", true));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_two", "ps_uid", "data_element_two_uid", true));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_three", "ps_uid", "data_element_three_uid", false));

        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_one_uid", "data_value_one"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_two_uid", "data_value_two"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_three_uid", "data_value_three"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_one_uid", "data_value_four"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_two_uid", "data_value_five"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_three_uid", "data_value_six"));

        ReportViewModel reportViewModelTwo = ReportViewModel.create("event_two",
                ReportViewModel.Status.SYNCED, Arrays.asList("data_element_one_name: data_value_four",
                        "data_element_two_name: data_value_five"));

        TestSubscriber<List<ReportViewModel>> testObserver = reportsRepository.reports().test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        ReportViewModel reportViewModelThree = ReportViewModel.create("event_three",
                ReportViewModel.Status.TO_SYNC, Arrays.asList("data_element_one_name: data_value_seven",
                        "data_element_two_name: data_value_eight"));

        // insert new item
        // open transaction in order to avoid notification bursting
        BriteDatabase.Transaction transaction = databaseRule.briteDatabase().newTransaction();
        databaseRule.briteDatabase().insert(EventModel.TABLE, event("event_three",
                BaseIdentifiableObject.DATE_FORMAT.parse("2017-09-06T00:05:57.495"),
                "organisation_unit_uid", "program_uid", "ps_uid", State.TO_POST));
        databaseRule.briteDatabase().insert(TrackedEntityDataValueModel.TABLE,
                dataValue("event_three", "data_element_one_uid", "data_value_seven"));
        databaseRule.briteDatabase().insert(TrackedEntityDataValueModel.TABLE,
                dataValue("event_three", "data_element_two_uid", "data_value_eight"));
        databaseRule.database().insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_three", "data_element_three_uid", "data_value_nine"));
        transaction.markSuccessful();
        transaction.end();

        testObserver.assertNoErrors();
        testObserver.assertValueCount(2);
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(1);
        assertThat(reports.size()).isEqualTo(3);
        assertThat(reports.get(0)).isEqualTo(reportViewModelThree);

        // mark event 3 as removed
        ContentValues updateEvent = new ContentValues();
        updateEvent.put(EventModel.Columns.STATE, State.TO_DELETE.toString());
        databaseRule.briteDatabase().update(EventModel.TABLE, updateEvent,
                EventModel.Columns.UID + " = ?", "event_three");

        testObserver.assertNoErrors();
        testObserver.assertValueCount(3);
        testObserver.assertNotComplete();

        // check if item was removed
        List<ReportViewModel> updatedReports = testObserver.values().get(2);
        assertThat(updatedReports.size()).isEqualTo(2);
        assertThat(updatedReports.get(0)).isEqualTo(reportViewModelTwo);
    }

    @Test
    public void reportsWithoutVisiblePsdeMustBePropagated() {
        SQLiteDatabase db = databaseRule.database();
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_one", "ps_uid", "data_element_one_uid", false));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_two", "ps_uid", "data_element_two_uid", false));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_three", "ps_uid", "data_element_three_uid", false));

        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_one_uid", "data_value_one"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_two_uid", "data_value_two"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_three_uid", "data_value_three"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_one_uid", "data_value_four"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_two_uid", "data_value_five"));
        db.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_three_uid", "data_value_six"));

        ReportViewModel reportViewModelOne = ReportViewModel.create("event_one",
                ReportViewModel.Status.TO_SYNC, Arrays.asList(""));

        ReportViewModel reportViewModelTwo = ReportViewModel.create("event_two",
                ReportViewModel.Status.SYNCED, Arrays.asList(""));

        TestSubscriber<List<ReportViewModel>> testObserver = reportsRepository.reports().test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);
        assertThat(reports.size()).isEqualTo(2);
        assertThat(reports.get(0)).isEqualTo(reportViewModelTwo);
        assertThat(reports.get(1)).isEqualTo(reportViewModelOne);
    }

    @Test
    public void reportsWithoutVisiblePsdeAndDataValuesMustBePropagated() {
        SQLiteDatabase db = databaseRule.database();
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_one", "ps_uid", "data_element_one_uid", false));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_two", "ps_uid", "data_element_two_uid", false));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_three", "ps_uid", "data_element_three_uid", false));

        ReportViewModel reportViewModelOne = ReportViewModel.create("event_one",
                ReportViewModel.Status.TO_SYNC, Arrays.asList(""));
        ReportViewModel reportViewModelTwo = ReportViewModel.create("event_two",
                ReportViewModel.Status.SYNCED, Arrays.asList(""));

        TestSubscriber<List<ReportViewModel>> testObserver = reportsRepository.reports().test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);
        assertThat(reports.size()).isEqualTo(2);
        assertThat(reports.get(0)).isEqualTo(reportViewModelTwo);
        assertThat(reports.get(1)).isEqualTo(reportViewModelOne);
    }

    @Test
    public void reportsWithoutDataValuesMustBePropagated() {
        SQLiteDatabase db = databaseRule.database();
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_one", "ps_uid", "data_element_one_uid", true));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_two", "ps_uid", "data_element_two_uid", true));
        db.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_three", "ps_uid", "data_element_three_uid", false));

        ReportViewModel reportViewModelOne = ReportViewModel.create("event_one",
                ReportViewModel.Status.TO_SYNC, Arrays.asList(
                        "data_element_one_name: -", "data_element_two_name: -"));
        ReportViewModel reportViewModelTwo = ReportViewModel.create("event_two",
                ReportViewModel.Status.SYNCED, Arrays.asList(
                        "data_element_one_name: -", "data_element_two_name: -"));

        TestSubscriber<List<ReportViewModel>> testObserver = reportsRepository.reports().test();

        testObserver.assertValueCount(1);
        testObserver.assertNoErrors();
        testObserver.assertNotComplete();

        List<ReportViewModel> reports = testObserver.values().get(0);
        assertThat(reports.size()).isEqualTo(2);
        assertThat(reports.get(0)).isEqualTo(reportViewModelTwo);
        assertThat(reports.get(1)).isEqualTo(reportViewModelOne);
    }

    private static ContentValues dataValue(String event, String dataelement, String value) {
        ContentValues dataValue = new ContentValues();
        dataValue.put(TrackedEntityDataValueModel.Columns.EVENT, event);
        dataValue.put(TrackedEntityDataValueModel.Columns.DATA_ELEMENT, dataelement);
        dataValue.put(TrackedEntityDataValueModel.Columns.VALUE, value);
        return dataValue;
    }

    private static ContentValues dataElement(String uid, String displayName) {
        ContentValues dataElementTwo = new ContentValues();
        dataElementTwo.put(DataElementModel.Columns.UID, uid);
        dataElementTwo.put(DataElementModel.Columns.DISPLAY_NAME, displayName);
        return dataElementTwo;
    }

    private static ContentValues programStageDataElement(String uid, String programStage,
            String dataElement, boolean showInReports) {
        ContentValues programStageDataElement = new ContentValues();
        programStageDataElement.put(ProgramStageDataElementModel.Columns.UID, uid);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.PROGRAM_STAGE, programStage);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.DATA_ELEMENT, dataElement);
        programStageDataElement.put(ProgramStageDataElementModel.Columns.DISPLAY_IN_REPORTS, showInReports ? 1 : 0);
        return programStageDataElement;
    }

    private static ContentValues event(String uid, Date created, String orgUnit,
            String program, String programStage, State state) {
        ContentValues event = new ContentValues();
        event.put(EventModel.Columns.UID, uid);
        event.put(EventModel.Columns.CREATED,
                BaseIdentifiableObject.DATE_FORMAT.format(created));
        event.put(EventModel.Columns.ORGANISATION_UNIT, orgUnit);
        event.put(EventModel.Columns.PROGRAM, program);
        event.put(EventModel.Columns.PROGRAM_STAGE, programStage);
        event.put(EventModel.Columns.STATE, state.toString());
        return event;
    }
}
