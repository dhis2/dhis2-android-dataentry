package org.hisp.dhis.android.dataentry.home;

import android.content.ContentValues;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
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

import java.util.Date;

import okhttp3.HttpUrl;

@RunWith(AndroidJUnit4.class)
public class HomeViewTests {
    private HomeRobot homeRobot;
    private ContentValues user;

    private DatabaseRule databaseRule = new DatabaseRule(((Components) InstrumentationRegistry.getTargetContext()
            .getApplicationContext()).appComponent().briteDatabase());
    private ActivityTestRule activityTestRule
            = new ActivityTestRule<>(HomeActivity.class, true, false);

    @Rule
    public RuleChain rules = RuleChain.emptyRuleChain()
            .around(databaseRule)
            .around(activityTestRule);

    @Before
    public void setUp() throws Exception {
        homeRobot = new HomeRobot();

        user = new ContentValues();
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

        activityTestRule.launchActivity(new Intent());
    }

    @Test
    public void drawerShouldContainCorrectInformationAboutUser() {
        homeRobot.openSlidingPanel()
                .checkUsername("test_first_name test_surname")
                .checkUserInitials("TT");
    }

    @Test
    public void drawerShouldObserveChangesInDatabase() {
        homeRobot.openSlidingPanel();

        user.put(UserModel.Columns.FIRST_NAME, "another_first_name");
        user.put(UserModel.Columns.SURNAME, "another_surname");

        databaseRule.briteDatabase().update(UserModel.TABLE, user,
                UserModel.Columns.UID + " = ?", String.valueOf("test_user_uid"));

        homeRobot
                .checkUsername("another_first_name another_surname")
                .checkUserInitials("AA");
    }

    @Test
    public void homeViewShouldHandleConfigurationChanges() {
        homeRobot.openSlidingPanel()
                .checkUsername("test_first_name test_surname")
                .checkUserInitials("TT")
                .rotateToLandscape()
                .checkUsername("test_first_name test_surname")
                .checkUserInitials("TT")
                .rotateToPortrait()
                .checkUsername("test_first_name test_surname")
                .checkUserInitials("TT");
    }

    // ToDo: implement tests which verify that correct fragments are attached when menu items are selected.
//    @Test
//    public void formsItemShouldBeSelectedByDefault() {
//        homeRobot.checkToolbarTitle(resources.getString(R.string.drawer_item_forms))
//                .openSlidingPanel()
//                .formsMenuItemIsSelected()
//                .rotateToLandscape();
//    }

    @After
    public void tearDown() throws Exception {
        ((DhisInstrumentationTestsApp) InstrumentationRegistry
                .getTargetContext().getApplicationContext()).clear();
    }
}
