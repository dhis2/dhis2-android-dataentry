package org.hisp.dhis.android.dataentry;

import org.hisp.dhis.android.dataentry.server.ServerComponent;
import org.hisp.dhis.android.dataentry.server.ServerModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    ServerComponent plus(ServerModule serverModule);
}
