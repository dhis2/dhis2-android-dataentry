package org.hisp.dhis.android.dataentry.launcher;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.dataentry.DhisInstrumentationTestsApp;
import org.hisp.dhis.android.dataentry.login.LoginActivity;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.HttpUrl;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class LauncherViewTests {

    @Rule
    public ActivityTestRule<LauncherActivity> launcherViewRule =
            new ActivityTestRule<>(LauncherActivity.class, true, false);

    @Test
    public void launcherView_shouldNavigateToLoginView_ifServerIsNotConfigured() {
        Intents.init();

        launcherViewRule.launchActivity(new Intent());
        intended(hasComponent(LoginActivity.class.getName()));

        Intents.release();
    }

    @Test
    public void launcherView_shouldNavigateToLoginView_ifUserIsNotSignedIn() {
        ConfigurationModel configuration = ConfigurationModel.builder()
                .serverUrl(HttpUrl.parse("https://play.dhis2.org/demo/"))
                .build();
        ((DhisInstrumentationTestsApp) InstrumentationRegistry.getTargetContext()
                .getApplicationContext()).createServerComponent(configuration);

        Intents.init();

        launcherViewRule.launchActivity(new Intent());
        intended(hasComponent(LoginActivity.class.getName()));

        Intents.release();
    }

    @After
    public void tearDown() throws Exception {
        ((DhisInstrumentationTestsApp) InstrumentationRegistry
                .getTargetContext().getApplicationContext()).clear();
    }
}
