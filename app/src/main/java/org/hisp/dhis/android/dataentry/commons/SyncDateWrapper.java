/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
