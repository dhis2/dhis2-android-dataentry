/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dataentry.login;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.squareup.spoon.Spoon;

import org.hisp.dhis.android.dataentry.DhisInstrumentationTestsApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.home.HomeActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class LoginViewTests {
    private static final String SERVER_URL = "http://play.dhis2.org/demo";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Rule
    public ActivityTestRule<LoginActivity> loginViewRule =
            new ActivityTestRule<>(LoginActivity.class);

    private MockWebServer mockWebServer;
    private LoginRobot loginRobot;

    @Before
    public void setUp() throws Exception {
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
    }

    @Test
    public void hintsShouldBeDisplayedWhenEmpty() {
        Resources resources = (InstrumentationRegistry.getTargetContext()).getResources();

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
        Spoon.screenshot(loginViewRule.getActivity(), "login_button_should_be_enabled");
    }

    @Test
    public void loginShouldSuccessfullyNavigateToHome() {
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
        mockWebServer.enqueue(mockResponse);

        Intents.init();
        loginRobot.typeServerUrl(SERVER_URL)
                .typeUsername(USERNAME)
                .typePassword(PASSWORD)
                .clickOnLoginButton();

        // if login is successful, home activity should be started
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();

        Spoon.screenshot(loginViewRule.getActivity(), "logged_in_state");
    }
}