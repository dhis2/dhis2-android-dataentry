package org.hisp.dhis.android.dataentry.dashboard.navigation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface EventSelection {

    void setSelectedEvent(@NonNull EventViewModel selectedEvent);

    @Nullable
    EventViewModel getSelectedEvent();
}
