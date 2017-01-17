package org.hisp.dhis.android.dataentry.server;

import dagger.Subcomponent;

@PerServer
@Subcomponent(
        modules = {
                ServerModule.class
        }
)
public interface ServerComponent {
    ConfigurationRepository configurationRepository();
}
