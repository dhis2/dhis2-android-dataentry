package org.hisp.dhis.android.dataentry.commons.utils;

import android.support.annotation.NonNull;

import java.util.Date;

public interface CurrentDateProvider {
    @NonNull
    Date currentDate();
}
