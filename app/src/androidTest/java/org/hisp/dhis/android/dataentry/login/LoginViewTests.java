package org.hisp.dhis.android.dataentry.login;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.squareup.spoon.Spoon;

import org.hisp.dhis.android.dataentry.DhisInstrumentationTestsApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.main.MainActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class LoginViewTests {
    private static final String SERVER_URL = "http://play.dhis2.org/demo";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Rule
    public ActivityTestRule<LoginActivity> loginViewRule =
            new ActivityTestRule<>(LoginActivity.class);

    private Resources resources;
    private MockWebServer mockWebServer;
    private LoginRobot loginRobot;

    @Before
    public void setUp() throws Exception {
        resources = (InstrumentationRegistry.getTargetContext()).getResources();

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        DhisInstrumentationTestsApp dhisApp = ((DhisInstrumentationTestsApp)
                InstrumentationRegistry.getTargetContext().getApplicationContext());
        dhisApp.overrideBaseUrl(mockWebServer.url("/"));

        loginRobot = new LoginRobot();
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();

        ((DhisInstrumentationTestsApp) InstrumentationRegistry
                .getTargetContext().getApplicationContext()).clear();
    }

    @Test
    public void hintsShouldBeDisplayedWhenEmpty() {
        // Note: this tests have to be adapted in case
        // if translations are added to the app
        loginRobot
                .checkServerUrlHint(resources.getString(R.string.server_url))
                .checkUsernameHint(resources.getString(R.string.username))
                .checkPasswordHint(resources.getString(R.string.password))
                .checkLoginButtonLabel(resources.getString(R.string.log_in));
    }

    @Test
    public void enableLoginButtonOnlyWhenAllFieldsAreFilled() {
        Spoon.screenshot(loginViewRule.getActivity(), "login_initial_state");

        // check if button is enabled when all fields are present
        loginRobot.typeServerUrl(SERVER_URL)
                .typeUsername(USERNAME)
                .typePassword(PASSWORD)
                .isLoginButtonEnabled();

        // try erasing / typing fields and check the
        // state of the button alongside
        loginRobot
                .eraseServerUrl().isLoginButtonDisabled().typeServerUrl(SERVER_URL)
                .eraseUsername().isLoginButtonDisabled().typeUsername(USERNAME)
                .erasePassword().isLoginButtonDisabled().typePassword(PASSWORD)
                .isLoginButtonEnabled();

        // perform configuration change and make sure that
        // views are in correct state
        loginRobot.rotateToLandscape()
                .checkServerUrl(SERVER_URL)
                .checkUsername(USERNAME)
                .checkPassword(PASSWORD)
                .isLoginButtonEnabled();

        Spoon.screenshot(loginViewRule.getActivity(), "login_button_should_be_enabled");
    }

    @Test
    public void loginShouldSuccessfullyNavigateToHome() throws InterruptedException {
        Spoon.screenshot(loginViewRule.getActivity(), "login_initial_state");

        MockResponse mockResponse = new MockResponse()
                .setBody("{\n" +
                        "\n" +
                        "    \"created\": \"2015-03-31T13:31:09.324\",\n" +
                        "    \"lastUpdated\": \"2016-04-06T00:05:57.495\",\n" +
                        "    \"name\": \"John Barnes\",\n" +
                        "    \"id\": \"DXyJmlo9rge\",\n" +
                        "    \"displayName\": \"John Barnes\",\n" +
                        "    \"firstName\": \"John\",\n" +
                        "    \"surname\": \"Barnes\",\n" +
                        "    \"email\": \"john@hmail.com\",\n" +
                        "    \"userCredentials\": {\n" +
                        "        \"lastUpdated\": \"2016-12-20T15:04:21.254\",\n" +
                        "        \"code\": \"android\",\n" +
                        "        \"created\": \"2015-03-31T13:31:09.206\",\n" +
                        "        \"name\": \"John Traore\",\n" +
                        "        \"id\": \"M0fCOxtkURr\",\n" +
                        "        \"displayName\": \"John Traore\",\n" +
                        "        \"username\": \"android\"\n" +
                        "    },\n" +
                        "    \"organisationUnits\": [\n" +
                        "        {\n" +
                        "            \"code\": \"OU_559\",\n" +
                        "            \"level\": 4,\n" +
                        "            \"created\": \"2012-02-17T15:54:39.987\",\n" +
                        "            \"lastUpdated\": \"2014-11-25T09:37:54.924\",\n" +
                        "            \"name\": \"Ngelehun CHC\",\n" +
                        "            \"id\": \"DiszpKrYNg8\",\n" +
                        "            \"shortName\": \"Ngelehun CHC\",\n" +
                        "            \"displayName\": \"Ngelehun CHC\",\n" +
                        "            \"displayShortName\": \"Ngelehun CHC\",\n" +
                        "            \"path\": \"/ImspTQPwCqd/O6uvpzGd5pu/YuQRtpLP10I/DiszpKrYNg8\",\n" +
                        "            \"openingDate\": \"1970-01-01T00:00:00.000\",\n" +
                        "            \"parent\": {\n" +
                        "                \"id\": \"YuQRtpLP10I\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "    ]\n" +
                        "\n" +
                        "}");
        mockResponse.setBodyDelay(2, TimeUnit.SECONDS);
        mockWebServer.enqueue(mockResponse);

        Intents.init();

        // kick of login
        loginRobot.typeServerUrl(SERVER_URL)
                .typeUsername(USERNAME)
                .typePassword(PASSWORD)
                .clickOnLoginButton();

        // if login is successful, home activity should be started
        Intents.intended(hasComponent(MainActivity.class.getName()));
        Intents.release();

        Spoon.screenshot(loginViewRule.getActivity(), "logged_in_state");
    }

    @Test
    public void loginShouldRenderWrongUsernameMessageIf401() {
        Spoon.screenshot(loginViewRule.getActivity(), "login_initial_state");

        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED);
        mockWebServer.enqueue(mockResponse);

        loginRobot.typeServerUrl(SERVER_URL)
                .typeUsername(USERNAME)
                .typePassword(PASSWORD)
                .clickOnLoginButton()
                .checkUsernameError(resources.getString(R.string.error_wrong_credentials))
                .checkPasswordError(resources.getString(R.string.error_wrong_credentials));

        Spoon.screenshot(loginViewRule.getActivity(), "wrong_server_url_state");
    }
}