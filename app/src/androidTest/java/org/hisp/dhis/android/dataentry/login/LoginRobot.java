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

class LoginRobot {
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

    LoginRobot progressBarIsVisible() {
        onView(withId(R.id.progress_bar_circular))
                .check(matches(isDisplayed()));
        return this;
    }

    LoginRobot serverUrlIsHidden() {
        onView(withId(R.id.edittext_server_url))
                .check(matches(not(isDisplayed())));
        return this;
    }

    LoginRobot usernameIsHidden() {
        onView(withId(R.id.edittext_username))
                .check(matches(not(isDisplayed())));
        return this;
    }

    LoginRobot passwordIsHidden() {
        onView(withId(R.id.edittext_password))
                .check(matches(not(isDisplayed())));
        return this;
    }

    LoginRobot loginButtonIsHidden() {
        onView(withId(R.id.edittext_password))
                .check(matches(not(isDisplayed())));
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
