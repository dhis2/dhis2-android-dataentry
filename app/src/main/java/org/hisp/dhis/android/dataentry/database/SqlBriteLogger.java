package org.hisp.dhis.android.dataentry.database;

import com.squareup.sqlbrite.SqlBrite;

import timber.log.Timber;

class SqlBriteLogger implements SqlBrite.Logger {
    private static final String TAG = SqlBriteLogger.class.getSimpleName();

    @Override
    public void log(String message) {
        Timber.tag(TAG);
        Timber.d(message);
    }
}
