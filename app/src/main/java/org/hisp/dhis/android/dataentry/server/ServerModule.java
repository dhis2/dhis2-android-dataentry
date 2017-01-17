package org.hisp.dhis.android.dataentry.server;

import android.content.Context;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationManagerFactory;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.Authenticator;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.dataentry.AppModule;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module(includes = AppModule.class)
public class ServerModule {
    private final String databaseName;
    private final ConfigurationModel configuration;

    public ServerModule(@NonNull String databaseName,
                        @NonNull ConfigurationModel configuration) {
        this.databaseName = databaseName;
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
    DbOpenHelper providesDbOpenHelper(Context context) {
        return new DbOpenHelper(context, databaseName);
    }

    @Provides
    @PerServer
    ConfigurationManager providesConfigurationManager(DbOpenHelper dbOpenHelper) {
        return ConfigurationManagerFactory.create(dbOpenHelper);
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
}
