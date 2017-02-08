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

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.espresso.OrientationChangeAction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hisp.dhis.android.dataentry.espresso.CustomViewMatchers.withErrorText;
import static org.hisp.dhis.android.dataentry.espresso.CustomViewMatchers.withHint;

public class LoginRobot {
    LoginRobot typeServerUrl(String serverUrl) {
        onView(withId(R.id.edittext_server_url))
                .perform(scrollTo(), typeText(serverUrl), closeSoftKeyboard());
        return this;
    }

    LoginRobot typeUsername(String username) {
        onView(withId(R.id.edittext_username))
                .perform(scrollTo(), typeText(username), closeSoftKeyboard());
        return this;
    }

    LoginRobot typePassword(String password) {
        onView(withId(R.id.edittext_password))
                .perform(scrollTo(), typeText(password), closeSoftKeyboard());
        return this;
    }

    LoginRobot checkServerUrlHint(String expectedServerUrlHint) {
        onView(withId(R.id.layout_edittext_server_url))
                .perform(scrollTo())
                .check(matches(withHint(is(expectedServerUrlHint))));
        return this;
    }

    LoginRobot checkUsernameHint(String expectedUsernameHint) {
        onView(withId(R.id.layout_edittext_username))
                .perform(scrollTo())
                .check(matches(withHint(is(expectedUsernameHint))));
        return this;
    }

    LoginRobot checkPasswordHint(String expectedPasswordHint) {
        onView(withId(R.id.layout_edittext_password))
                .perform(scrollTo())
                .check(matches(withHint(is(expectedPasswordHint))));
        return this;
    }

    LoginRobot checkServerUrl(String expectedServerUrl) {
        onView(withId(R.id.edittext_server_url))
                .perform(scrollTo())
                .check(matches(withText(is(expectedServerUrl))));
        return this;
    }

    LoginRobot checkUsername(String expectedUsername) {
        onView(withId(R.id.edittext_username))
                .perform(scrollTo())
                .check(matches(withText(is(expectedUsername))));
        return this;
    }

    LoginRobot checkPassword(String expectedPassword) {
        onView(withId(R.id.edittext_password))
                .perform(scrollTo())
                .check(matches(withText(is(expectedPassword))));
        return this;
    }

    LoginRobot checkServerUrlError(String errorMessage) {
        onView(withId(R.id.edittext_server_url))
                .perform(scrollTo())
                .check(matches(withErrorText(is(errorMessage))));
        return this;
    }

    LoginRobot checkUsernameError(String errorMessage) {
        onView(withId(R.id.edittext_username))
                .perform(scrollTo())
                .check(matches(withErrorText(is(errorMessage))));
        return this;
    }

    LoginRobot checkPasswordError(String errorMessage) {
        onView(withId(R.id.edittext_password))
                .perform(scrollTo())
                .check(matches(withErrorText(is(errorMessage))));
        return this;
    }

    LoginRobot checkLoginButtonLabel(String expectedLoginButtonLabel) {
        onView(withId(R.id.button_log_in))
                .perform(scrollTo())
                .check(matches(withText(expectedLoginButtonLabel)));
        return this;
    }

    LoginRobot eraseServerUrl() {
        onView(withId(R.id.edittext_server_url))
                .perform(scrollTo(), clearText());
        return this;
    }

    LoginRobot eraseUsername() {
        onView(withId(R.id.edittext_username))
                .perform(scrollTo(), clearText());
        return this;
    }

    LoginRobot erasePassword() {
        onView(withId(R.id.edittext_password))
                .perform(scrollTo(), clearText());
        return this;
    }

    LoginRobot isLoginButtonEnabled() {
        onView(withId(R.id.button_log_in))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));
        return this;
    }

    LoginRobot isLoginButtonDisabled() {
        onView(withId(R.id.button_log_in))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));
        return this;
    }

    LoginRobot rotateToPortrait() {
        onView(isRoot()).perform(OrientationChangeAction.orientationPortrait());
        return this;
    }

    LoginRobot rotateToLandscape() {
        onView(isRoot()).perform(OrientationChangeAction.orientationLandscape());
        return this;
    }

    LoginRobot clickOnLoginButton() {
        onView(withId(R.id.button_log_in)).perform(scrollTo(), click());
        return this;
    }
}
