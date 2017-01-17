package org.hisp.dhis.android.dataentry.server;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.Authenticator;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
@PerServer
public class ServerModule {
    private final ConfigurationModel configuration;

    public ServerModule(@NonNull ConfigurationModel configuration) {
        this.configuration = configuration;
    }

    @Provides
    @PerServer
    public D2 providesSdkInstance(DbOpenHelper openHelper, OkHttpClient client) {
        return new D2.Builder()
                .configuration(configuration)
                .dbOpenHelper(openHelper)
                .okHttpClient(client)
                .build();
    }

    @Provides
    @PerServer
    Authenticator providesAuthenticator(DbOpenHelper dbOpenHelper) {
        return BasicAuthenticatorFactory.create(dbOpenHelper);
    }

    @Provides
    @PerServer
    OkHttpClient providesOkHttpClient(Authenticator authenticator) {
        return new OkHttpClient.Builder()
                .addInterceptor(authenticator)
                .build();
    }

    @Provides
    @PerServer
    ConfigurationRepository providesConfigurationRepository(D2 d2) {
        return new ConfigurationRepositoryImpl(d2);
    }
}
