package org.hisp.dhis.android.dataentry;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.hisp.dhis.android.dataentry.server.ServerComponent;
import org.hisp.dhis.android.dataentry.server.ServerModule;
import org.hisp.dhis.android.dataentry.utils.CrashReportingTree;

import hu.supercluster.paperwork.Paperwork;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class DhisApp extends Application {
    private static final String DATABASE_NAME = "dhis.db";
    private static final String GIT_SHA = "gitSha";
    private static final String BUILD_DATE = "buildDate";

    private AppComponent appComponent;
    private ServerComponent serverComponent;
    private RefWatcher refWatcher;

    public static RefWatcher refWatcher(Context context) {
        return ((DhisApp) context.getApplicationContext()).refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // fail early on leaks
        setUpStrictMode();

        Paperwork paperwork = setUpPaperwork();
        setUpFabric(paperwork);
        setUpTimber();

        appComponent = prepareAppComponent().build();
        refWatcher = setUpLeakCanary();
    }

    public static AppComponent getAppComponent(@NonNull Context context) {
        return ((DhisApp) context.getApplicationContext()).appComponent;
    }

    public static ServerComponent createServerComponent(@NonNull Context context,
                                                        @NonNull String serverUrl) {
        DhisApp dhisApp = ((DhisApp) context.getApplicationContext());
        dhisApp.serverComponent = dhisApp.appComponent.plus(new ServerModule(serverUrl));
        return dhisApp.serverComponent;
    }

    protected DaggerAppComponent.Builder prepareAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this, DATABASE_NAME));
    }

    @NonNull
    private Paperwork setUpPaperwork() {
        return new Paperwork(this);
    }

    @NonNull
    private RefWatcher setUpLeakCanary() {
        if (BuildConfig.DEBUG) {
            return LeakCanary.install(this);
        } else {
            return RefWatcher.DISABLED;
        }
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
}
