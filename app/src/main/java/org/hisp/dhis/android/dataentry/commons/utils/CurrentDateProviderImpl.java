package org.hisp.dhis.android.dataentry.commons.utils;

import android.support.annotation.NonNull;

import java.util.Date;

public final class CurrentDateProviderImpl implements CurrentDateProvider {
    public CurrentDateProviderImpl() {
        // explicit constructor
    }

    @NonNull
    @Override
    public Date currentDate() {
        return new Date();
    }
}
