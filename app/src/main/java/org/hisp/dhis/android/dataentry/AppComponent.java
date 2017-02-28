package org.hisp.dhis.android.dataentry;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.database.DbModule;
import org.hisp.dhis.android.dataentry.launcher.LauncherComponent;
import org.hisp.dhis.android.dataentry.launcher.LauncherModule;
import org.hisp.dhis.android.dataentry.login.LoginComponent;
import org.hisp.dhis.android.dataentry.login.LoginModule;
import org.hisp.dhis.android.dataentry.server.ServerComponent;
import org.hisp.dhis.android.dataentry.server.ServerModule;
import org.hisp.dhis.android.dataentry.utils.SchedulerModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class, DbModule.class, SchedulerModule.class,
})
public interface AppComponent {
    // exposing objects for testing
    BriteDatabase briteDatabase();

    // injection targets
    void inject(DhisApp dhisApp);

    // sub-components
    ServerComponent plus(ServerModule serverModule);

    LauncherComponent plus(LauncherModule launcherModule);

    LoginComponent plus(LoginModule loginModule);
}
