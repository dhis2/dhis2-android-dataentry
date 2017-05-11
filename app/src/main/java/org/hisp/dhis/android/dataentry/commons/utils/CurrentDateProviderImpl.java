package org.hisp.dhis.android.dataentry.commons.utils;

import android.support.annotation.NonNull;

import java.util.Date;

final class CurrentDateProviderImpl implements CurrentDateProvider {

    @NonNull
    @Override
    public Date currentDate() {
        return new Date();
    }
}
