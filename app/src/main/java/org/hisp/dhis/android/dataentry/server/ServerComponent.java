package org.hisp.dhis.android.dataentry.server;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.dataentry.commons.dagger.PerServer;
import org.hisp.dhis.android.dataentry.user.UserComponent;
import org.hisp.dhis.android.dataentry.user.UserModule;

import dagger.Subcomponent;

@PerServer
@Subcomponent(modules = ServerModule.class)
public interface ServerComponent {

    @NonNull
    UserManager userManager();

    @NonNull
    UserComponent plus(@NonNull UserModule userModule);
}
