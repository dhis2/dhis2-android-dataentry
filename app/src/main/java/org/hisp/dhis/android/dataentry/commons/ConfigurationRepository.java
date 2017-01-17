package org.hisp.dhis.android.dataentry.commons;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.configuration.ConfigurationModel;

import io.reactivex.Observable;

public interface ConfigurationRepository {
    @NonNull
    Observable<ConfigurationModel> configuration();
}
