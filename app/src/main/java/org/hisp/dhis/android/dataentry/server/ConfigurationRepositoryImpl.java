package org.hisp.dhis.android.dataentry.server;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;

import io.reactivex.Observable;
import okhttp3.HttpUrl;

public class ConfigurationRepositoryImpl implements ConfigurationRepository {
    @NonNull
    private final ConfigurationManager configurationManager;

    public ConfigurationRepositoryImpl(@NonNull ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    @NonNull
    @Override
    public Observable<ConfigurationModel> configure(@NonNull HttpUrl baseUrl) {
        return Observable.defer(() -> Observable.fromCallable(
                () -> configurationManager.configure(baseUrl)));
    }

    @NonNull
    @Override
    public Observable<ConfigurationModel> get() {
        return Observable.defer(() -> {
            ConfigurationModel configuration = configurationManager.get();
            if (configuration != null) {
                return Observable.just(configuration);
            }

            return Observable.empty();
        });
    }

    @NonNull
    @Override
    public Observable<Integer> remove() {
        return Observable.defer(() -> Observable.fromCallable(() -> configurationManager.remove()));
    }
}
