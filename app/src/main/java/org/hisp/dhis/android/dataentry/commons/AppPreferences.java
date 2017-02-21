package org.hisp.dhis.android.dataentry.commons;

import java.text.ParseException;
import java.util.Date;

public interface AppPreferences {

    Date getLastSynced() throws ParseException;

    boolean setLastSynced(Date date);

    void setBackgroundSyncFrequency(int frequency);

    int getBackgroundSyncFrequency();

    void setBackgroundSyncState(Boolean enabled);

    void setSyncNotifications(Boolean enabled);

    boolean getSyncNotifications();

    boolean getBackgroundSyncState();

    boolean getCrashReportsState();

    void setCrashReportsState(Boolean enabled);
}
