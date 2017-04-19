package org.hisp.dhis.android.dataentry.reports;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.UserCredentialsModel;
import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.dataentry.Components;
import org.hisp.dhis.android.dataentry.DhisInstrumentationTestsApp;
import org.hisp.dhis.android.dataentry.rules.DatabaseRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import okhttp3.HttpUrl;

@RunWith(AndroidJUnit4.class)
public class ReportsViewTests {
    private ReportsRobot reportsRobot;

    // see persistFakeEvents()
    private ReportViewModel reportViewModelOne;
    private ReportViewModel reportViewModelTwo;

    private DatabaseRule databaseRule = new DatabaseRule(((Components) InstrumentationRegistry
            .getTargetContext().getApplicationContext()).appComponent().briteDatabase());
    private ActivityTestRule activityTestRule = new ActivityTestRule<>(ReportsActivity.class, true, false);

    @Rule
    public RuleChain rules = RuleChain.emptyRuleChain()
            .around(databaseRule)
            .around(activityTestRule);

    @Before
    public void setUp() throws Exception {
        reportsRobot = new ReportsRobot();

        reportViewModelOne = ReportViewModel.create("event_one",
                ReportViewModel.Status.SYNCED, Arrays.asList(
                        String.format(Locale.US, "%s: %s", "data_element_one_uid", "data_value_one"),
                        String.format(Locale.US, "%s: %s", "data_element_two_uid", "data_value_two"),
                        String.format(Locale.US, "%s: %s", "data_element_three_uid", "data_value_three")
                ));
        reportViewModelTwo = ReportViewModel.create("event_two",
                ReportViewModel.Status.TO_SYNC, Arrays.asList(
                        String.format(Locale.US, "%s: %s", "data_element_one_uid", "data_value_four"),
                        String.format(Locale.US, "%s: %s", "data_element_two_uid", "data_value_five"),
                        String.format(Locale.US, "%s: %s", "data_element_three_uid", "data_value_six")
                ));

        authenticateUser();
        persistFakeProgram();
        persistFakeEvents();

        // launch new activity for events
        Intent intent = new Intent();
        intent.putExtra(ReportsActivity.ARG_ARGUMENTS,
                ReportsArguments.createForEvents("program_uid", "program_form_name"));
        activityTestRule.launchActivity(intent);
    }

    @Test
    public void existingReportsShouldBeRendered() {
        reportsRobot.checkToolbarTitle("program_form_name");
    }

    @Test
    public void updatesShouldBePropagatedToTheView() {
        // ToDo
    }

    @Test
    public void clickOnReportShouldNavigateFurther() {
        // ToDo
    }

    @After
    public void tearDown() throws Exception {
        ((DhisInstrumentationTestsApp) InstrumentationRegistry.getTargetContext()
                .getApplicationContext()).clear();
    }


    /////////////////////////////////////////////////////////////////////
    // Helper methods
    /////////////////////////////////////////////////////////////////////

    private void authenticateUser() {
        ContentValues user = new ContentValues();
        user.put(UserModel.Columns.ID, 22L);
        user.put(UserModel.Columns.UID, "test_user_uid");
        user.put(UserModel.Columns.CODE, "test_code");
        user.put(UserModel.Columns.NAME, "test_name");
        user.put(UserModel.Columns.DISPLAY_NAME, "test_display_name");
        user.put(UserModel.Columns.CREATED, BaseIdentifiableObject.DATE_FORMAT.format(new Date()));
        user.put(UserModel.Columns.LAST_UPDATED, BaseIdentifiableObject.DATE_FORMAT.format(new Date()));
        user.put(UserModel.Columns.BIRTHDAY, "test_birthday");
        user.put(UserModel.Columns.EDUCATION, "test_education");
        user.put(UserModel.Columns.GENDER, "test_gender");
        user.put(UserModel.Columns.JOB_TITLE, "test_job_title");
        user.put(UserModel.Columns.SURNAME, "test_surname");
        user.put(UserModel.Columns.FIRST_NAME, "test_first_name");
        user.put(UserModel.Columns.INTRODUCTION, "test_introduction");
        user.put(UserModel.Columns.EMPLOYER, "test_employer");
        user.put(UserModel.Columns.INTERESTS, "test_interests");
        user.put(UserModel.Columns.LANGUAGES, "test_languages");
        user.put(UserModel.Columns.EMAIL, "test_email");
        user.put(UserModel.Columns.PHONE_NUMBER, "test_phone_number");
        user.put(UserModel.Columns.NATIONALITY, "test_nationality");

        ContentValues userCredentials = new ContentValues();
        userCredentials.put(UserCredentialsModel.Columns.ID, 22L);
        userCredentials.put(UserCredentialsModel.Columns.UID, "test_user_credentials_uid");
        userCredentials.put(UserCredentialsModel.Columns.CODE, "test_user_credentials_code");
        userCredentials.put(UserCredentialsModel.Columns.NAME, "test_user_credentials_name");
        userCredentials.put(UserCredentialsModel.Columns.DISPLAY_NAME, "test_user_credentials_display_name");
        userCredentials.put(UserCredentialsModel.Columns.CREATED, BaseIdentifiableObject.DATE_FORMAT.format(new Date()));
        userCredentials.put(UserCredentialsModel.Columns.LAST_UPDATED, BaseIdentifiableObject.DATE_FORMAT.format(new Date()));
        userCredentials.put(UserCredentialsModel.Columns.USERNAME, "test_username");
        userCredentials.put(UserCredentialsModel.Columns.USER, "test_user_uid");

        ContentValues authenticatedUser = new ContentValues();
        authenticatedUser.put(AuthenticatedUserModel.Columns.ID, 22L);
        authenticatedUser.put(AuthenticatedUserModel.Columns.USER, "test_user_uid");
        authenticatedUser.put(AuthenticatedUserModel.Columns.CREDENTIALS, "test_user_credentials_uid");

        databaseRule.database().insert(UserModel.TABLE, null, user);
        databaseRule.database().insert(UserCredentialsModel.TABLE, null, userCredentials);
        databaseRule.database().insert(AuthenticatedUserModel.TABLE, null, authenticatedUser);

        ConfigurationModel configurationModel = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse("https://play.dhis2.org/demo/"))
                .build();
        Components components = ((Components) InstrumentationRegistry
                .getTargetContext().getApplicationContext());
        components.createServerComponent(configurationModel);
        components.createUserComponent();
    }

    private void persistFakeProgram() throws ParseException {
        SQLiteDatabase database = databaseRule.database();

        ContentValues orgUnit = new ContentValues();
        orgUnit.put(OrganisationUnitModel.Columns.UID, "organisation_unit_uid");
        database.insert(OrganisationUnitModel.TABLE, null, orgUnit);

        ContentValues program = new ContentValues();
        program.put(ProgramModel.Columns.UID, "program_uid");
        database.insert(ProgramModel.TABLE, null, program);

        ContentValues programStage = new ContentValues();
        programStage.put(ProgramStageModel.Columns.UID, "ps_uid");
        programStage.put(ProgramStageModel.Columns.PROGRAM, "program_uid");
        database.insert(ProgramStageModel.TABLE, null, programStage);

        database.insert(DataElementModel.TABLE, null,
                dataElement("data_element_one_uid", "data_element_one_name"));
        database.insert(DataElementModel.TABLE, null,
                dataElement("data_element_two_uid", "data_element_two_name"));
        database.insert(DataElementModel.TABLE, null,
                dataElement("data_element_three_uid", "data_element_three_name"));

        database.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_one", "ps_uid", "data_element_one_uid", true));
        database.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_two", "ps_uid", "data_element_two_uid", true));
        database.insert(ProgramStageDataElementModel.TABLE, null,
                programStageDataElement("ps_data_element_three", "ps_uid", "data_element_three_uid", false));
    }

    private void persistFakeEvents() throws ParseException {
        SQLiteDatabase database = databaseRule.database();

        database.insert(EventModel.TABLE, null, event("event_one",
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"),
                "organisation_unit_uid", "program_uid", "ps_uid", State.TO_POST));
        database.insert(EventModel.TABLE, null, event("event_two",
                BaseIdentifiableObject.DATE_FORMAT.parse("2017-04-06T00:05:57.495"),
                "organisation_unit_uid", "program_uid", "ps_uid", State.SYNCED));

        database.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_one_uid", "data_value_one"));
        database.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_two_uid", "data_value_two"));
        database.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_one", "data_element_three_uid", "data_value_three"));
        database.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_one_uid", "data_value_four"));
        database.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_two_uid", "data_value_five"));
        database.insert(TrackedEntityDataValueModel.TABLE, null,
                dataValue("event_two", "data_element_three_uid", "data_value_six"));
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
