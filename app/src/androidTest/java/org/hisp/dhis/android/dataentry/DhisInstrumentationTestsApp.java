package org.hisp.dhis.android.dataentry;

import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;

import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.dataentry.commons.database.DbModule;
import org.hisp.dhis.android.dataentry.server.ServerComponent;
import org.hisp.dhis.android.dataentry.utils.IdlingSchedulerProvider;
import org.hisp.dhis.android.dataentry.utils.SchedulerModule;

import okhttp3.HttpUrl;

public class DhisInstrumentationTestsApp extends DhisApp {
    private HttpUrl baseUrl;

    @NonNull
    @Override
    protected DaggerAppComponent.Builder prepareAppComponent() {
        return super.prepareAppComponent()
                .dbModule(new DbModule(null))
                .schedulerModule(new SchedulerModule(new IdlingSchedulerProvider()));
    }

    @NonNull
    @Override
    public ServerComponent createServerComponent(@NonNull ConfigurationModel configuration) {
        if (baseUrl != null) {
            // base url set through overrideBaseUrl() should prioritized
            return super.createServerComponent(
                    ConfigurationModel.builder()
                            .serverUrl(baseUrl)
                            .build());
        }

        return super.createServerComponent(configuration);
    }

    public void overrideBaseUrl(@NonNull HttpUrl baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void clear() {
        // clear all idling resources
        Espresso.unregisterIdlingResources(Espresso.getIdlingResources()
                .toArray(new IdlingResource[Espresso.getIdlingResources().size()]));

        // release components
        releaseServerComponent();
        releaseLoginComponent();
        releaseUserComponent();

        // reset database state and
        // recreate application component
        appComponent().briteDatabase().close();
        createAppComponent();
    }
}
