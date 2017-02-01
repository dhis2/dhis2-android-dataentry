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

import android.app.Application;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.dataentry.server.ServerComponent;
import org.hisp.dhis.android.dataentry.server.ServerModule;
import org.hisp.dhis.android.dataentry.utils.CrashReportingTree;
import org.hisp.dhis.android.dataentry.utils.SchedulerModule;
import org.hisp.dhis.android.dataentry.utils.SchedulersProviderImpl;

import javax.inject.Inject;

import hu.supercluster.paperwork.Paperwork;
import io.fabric.sdk.android.Fabric;
import okhttp3.HttpUrl;
import timber.log.Timber;

// ToDo: Implement more tests for launcher activity, presenter
// ToDo: Do not allow wrong / malformed urls to be persisted in configuration manager
public class DhisApp extends Application {
    private static final String DATABASE_NAME = "dhis.db";
    private static final String GIT_SHA = "gitSha";
    private static final String BUILD_DATE = "buildDate";

    @Inject
    Paperwork paperwork;

    @Inject
    ConfigurationManager configurationManager;

    @Nullable
    AppComponent appComponent;

    @Nullable
    ServerComponent serverComponent;

    @Nullable
    RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        // application component
        setUpAppComponent();
        setUpServerComponent();

        setUpLeakCanary();
        setUpFabric();
        setUpTimber();

        // do not allow to do work on main thread
        setUpStrictMode();
    }

    private void setUpLeakCanary() {
        if (BuildConfig.DEBUG) {
            refWatcher = LeakCanary.install(this);
        } else {
            refWatcher = RefWatcher.DISABLED;
        }
    }

    private void setUpFabric() {
        if (BuildConfig.DEBUG) {
            // Set up Crashlytics, disabled for debug builds
            Crashlytics crashlyticsKit = new Crashlytics.Builder()
                    .core(new CrashlyticsCore.Builder()
                            .disabled(true)
                            .build())
                    .build();

            // Initialize Fabric with the debug-disabled crashlytics.
            Fabric.with(this, crashlyticsKit);
        } else {
            Crashlytics crashlytics = new Crashlytics();
            crashlytics.core.setString(GIT_SHA, paperwork.get(GIT_SHA));
            crashlytics.core.setString(BUILD_DATE, paperwork.get(BUILD_DATE));

            Fabric.with(this, crashlytics, new Answers());
        }
    }

    private void setUpTimber() {
        if (BuildConfig.DEBUG) {
            // Verbose logging for debug builds.
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private void setUpStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }

    private void setUpAppComponent() {
        appComponent = prepareAppComponent().build();
        appComponent.inject(this);
    }

    private void setUpServerComponent() {
        ConfigurationModel configuration = configurationManager.get();
        if (configuration != null) {
            serverComponent = appComponent.plus(new ServerModule(configuration.serverUrl()));
        }
    }

    @NonNull
    protected DaggerAppComponent.Builder prepareAppComponent() {
        return DaggerAppComponent.builder()
                .dbModule(new DbModule(DATABASE_NAME))
                .appModule(new AppModule(this))
                .schedulerModule(new SchedulerModule(new SchedulersProviderImpl()));
    }

    public ServerComponent createServerComponent(@NonNull HttpUrl baseUrl) {
        return (serverComponent = appComponent.plus(new ServerModule(baseUrl)));
    }

    @Nullable
    public ServerComponent serverComponent() {
        return serverComponent;
    }

    public AppComponent appComponent() {
        return appComponent;
    }

    public RefWatcher refWatcher() {
        return refWatcher;
    }
}
