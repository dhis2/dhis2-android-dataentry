package org.hisp.dhis.android.dataentry.commons;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;

import io.reactivex.Observable;

public class ConfigurationRepositoryImpl implements ConfigurationRepository {
    private final ConfigurationManager configurationManager;

    public ConfigurationRepositoryImpl(@NonNull ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    @NonNull
    @Override
    public Observable<ConfigurationModel> configuration() {
        return Observable.defer(() ->
                Observable.fromCallable(configurationManager::get)
        );
    }
}
