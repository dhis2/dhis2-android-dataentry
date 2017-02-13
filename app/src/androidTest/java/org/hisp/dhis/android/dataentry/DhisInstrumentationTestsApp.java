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

package org.hisp.dhis.android.dataentry;

import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;

import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.dataentry.database.DbModule;
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
