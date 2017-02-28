package org.hisp.dhis.android.dataentry.commons;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.android.dataentry.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;

import static org.hisp.dhis.android.dataentry.utils.Preconditions.isNull;


public class AppPreferencesImpl implements AppPreferences {
    public final static String PREFS_NAME = "preferences:application";

    public static final String CRASH_REPORTS = "crashReports";
    public static final String SYNC_DATE = "syncDate";
    public static final String UPDATE_FREQUENCY = "update_frequency";
    public static final String BACKGROUND_SYNC = "background_sync";
    public static final String SYNC_NOTIFICATIONS = "sync_notifications";

    //Default values:
    public static final int DEFAULT_UPDATE_FREQUENCY = 1440; // (1 day in minutes)
    public static final Boolean DEFAULT_BACKGROUND_SYNC = true;
    public static final Boolean DEFAULT_CRASH_REPORTS = true;
    public static final Boolean DEFAULT_SYNC_NOTIFICATIONS = true;

    private final SharedPreferences sharedPreferences;

    public AppPreferencesImpl(Context context) {
        isNull(context, "context must not be null");
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public Date getLastSynced() throws ParseException {
        return DateUtils.parseDate(sharedPreferences.getString(SYNC_DATE, new Date(0).toString()));
    }

    @Override
    public boolean setLastSynced(Date date) {
        return sharedPreferences.edit().putString(SYNC_DATE, date.toString()).commit();
    }

    @Override
    public void setBackgroundSyncFrequency(int minutes) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(UPDATE_FREQUENCY, minutes);
        editor.apply();
    }

    @Override
    public int getBackgroundSyncFrequency() {
        return sharedPreferences.getInt(UPDATE_FREQUENCY, DEFAULT_UPDATE_FREQUENCY);
    }

    @Override
    public void setBackgroundSyncState(Boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BACKGROUND_SYNC, enabled);
        editor.apply();
    }

    @Override
    public void setSyncNotifications(Boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SYNC_NOTIFICATIONS, enabled);
        editor.apply();
    }

    @Override
    public boolean getSyncNotifications() {
        return sharedPreferences.getBoolean(SYNC_NOTIFICATIONS, DEFAULT_SYNC_NOTIFICATIONS);
    }

    @Override
    public boolean getBackgroundSyncState() {
        return sharedPreferences.getBoolean(BACKGROUND_SYNC, DEFAULT_BACKGROUND_SYNC);
    }

    @Override
    public boolean getCrashReportsState() {
        return sharedPreferences.getBoolean(CRASH_REPORTS, DEFAULT_CRASH_REPORTS);
    }

    @Override
    public void setCrashReportsState(Boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CRASH_REPORTS, enabled);
        editor.apply();
    }
}