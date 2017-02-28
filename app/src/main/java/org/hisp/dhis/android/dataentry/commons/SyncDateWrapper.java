package org.hisp.dhis.android.dataentry.commons;

import android.support.annotation.Nullable;

import org.hisp.dhis.android.dataentry.utils.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SyncDateWrapper {
    //Constants:
    //    private static final long DAYS_OLD = 1L;
    private static final long NEVER = 0L;

    //    private final String DATE_FORMAT;
    private static final String NEVER_SYNCED = "never";
    private static final String MIN_AGO = "m ago";
    private static final String HOURS = "h";
    private static final String NOW = "now";

    private final AppPreferences appPreferences;

    public SyncDateWrapper(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
//        DATE_FORMAT = context.getString(R.string.date_format);
    }

    public void setLastSyncedNow() {
        appPreferences.setLastSynced(Calendar.getInstance().getTime());
    }

    public void clearLastSynced() {
        appPreferences.setLastSynced(new Date(0));
    }

    @Nullable
    public Date getLastSyncedDate() throws ParseException {
        return appPreferences.getLastSynced();
//        long lastSynced = appPreferences.getLastSynced();
//
//        if (lastSynced > NEVER) {
//            return new DateTime().withMillis(lastSynced);
//        }
//        return null;
    }

    public Date getLastSyncedDateObject() throws ParseException {
        return appPreferences.getLastSynced();
    }

    public String getLastSyncedString() throws ParseException {
        Date lastSync = getLastSyncedDateObject();

        if (lastSync.getTime() == NEVER) {
            return NEVER_SYNCED;
        }

        Date now = Calendar.getInstance().getTime();
        Map<TimeUnit, Long> map = DateUtils.computeDiff(lastSync, now);

        if (map.get(TimeUnit.DAYS) > 0) {
            return DateUtils.getDateFormat().format(lastSync);
        } else if (map.get(TimeUnit.HOURS) > 0) {
            return (map.get(TimeUnit.HOURS) + " " + HOURS);
        } else if (map.get(TimeUnit.MINUTES) > 0) {
            return (map.get(TimeUnit.MINUTES) + " " + MIN_AGO);
        } else {
            return NOW;
        }
    }
}
