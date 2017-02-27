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

package org.hisp.dhis.android.dataentry.main.home;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

import static org.hisp.dhis.android.dataentry.main.home.HomeEntity.Columns.DISPLAY_NAME;
import static org.hisp.dhis.android.dataentry.main.home.HomeEntity.Columns.ENTITY_TYPE;
import static org.hisp.dhis.android.dataentry.main.home.HomeEntity.Columns.UID;

public class HomeEntity implements Parcelable {
    private final String id;
    private final String title;
    private final HomeEntityType type;

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        static final String UID = "uid";
        static final String DISPLAY_NAME = "displayName";
        public static final String ENTITY_TYPE = "entityType";
    }

    public HomeEntity(String id, String title, HomeEntityType type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    protected HomeEntity(Parcel in) {
        id = in.readString();
        title = in.readString();
        type = HomeEntityType.valueOf(in.readString());
    }

    public static final Creator<HomeEntity> CREATOR = new Creator<HomeEntity>() {
        @Override
        public HomeEntity createFromParcel(Parcel in) {
            return new HomeEntity(in);
        }

        @Override
        public HomeEntity[] newArray(int size) {
            return new HomeEntity[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public HomeEntityType getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(type.name());
    }

    public static HomeEntity fromCursor(Cursor cursor) {

        int uidColumnIndex = cursor.getColumnIndex(UID);
        String uid = uidColumnIndex == -1 || cursor.isNull(uidColumnIndex) ? null : cursor.getString(uidColumnIndex);

        int displayNameColumnIndex = cursor.getColumnIndex(DISPLAY_NAME);
        String displayName = displayNameColumnIndex == -1 || cursor.isNull(displayNameColumnIndex) ?
                null : cursor.getString(displayNameColumnIndex);

        int entityTypeColumnIndex = cursor.getColumnIndex(ENTITY_TYPE);
        HomeEntityType entityType = entityTypeColumnIndex == -1 || cursor.isNull(entityTypeColumnIndex) ?
                null : HomeEntityType.valueOf(cursor.getString(entityTypeColumnIndex));

        return new HomeEntity(uid, displayName, entityType);
    }

    public enum HomeEntityType {
        PROGRAM,
        TRACKED_ENTITY
    }
}
