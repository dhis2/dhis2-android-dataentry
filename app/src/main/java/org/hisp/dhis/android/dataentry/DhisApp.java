package org.hisp.dhis.android.dataentry;

import android.app.Application;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;

import hu.supercluster.paperwork.Paperwork;
import io.fabric.sdk.android.Fabric;

public class DhisApp extends Application {
    private static final String GIT_SHA = "gitSha";
    private static final String BUILD_DATE = "buildDate";

    @Override
    public void onCreate() {
        super.onCreate();

        // fail early on leaks
        setStrictMode();

        Paperwork paperwork = setUpPaperwork();
        setUpFabric(paperwork);
    }

    @NonNull
    private Paperwork setUpPaperwork() {
        return new Paperwork(this);
    }

    private void setUpFabric(@NonNull Paperwork paperwork) {
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

            Fabric.with(this, new Crashlytics(), new Answers());
        }
    }

    private void setStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }
}
