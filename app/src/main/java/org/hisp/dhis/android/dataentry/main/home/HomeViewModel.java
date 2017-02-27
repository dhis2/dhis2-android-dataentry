package org.hisp.dhis.android.dataentry.main.home;

import android.database.Cursor;
import android.os.Parcel;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

import static org.hisp.dhis.android.dataentry.main.home.HomeViewModel.Columns.DISPLAY_NAME;
import static org.hisp.dhis.android.dataentry.main.home.HomeViewModel.Columns.ENTITY_TYPE;
import static org.hisp.dhis.android.dataentry.main.home.HomeViewModel.Columns.UID;

public class HomeViewModel {
    private final String id;
    private final String title;
    private final HomeEntityType type;

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        static final String UID = "uid";
        static final String DISPLAY_NAME = "displayName";
        public static final String ENTITY_TYPE = "entityType";
    }

    public HomeViewModel(String id, String title, HomeEntityType type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    protected HomeViewModel(Parcel in) {
        id = in.readString();
        title = in.readString();
        type = HomeEntityType.valueOf(in.readString());
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public HomeEntityType getType() {
        return type;
    }

    public static HomeViewModel fromCursor(Cursor cursor) {

        int uidColumnIndex = cursor.getColumnIndex(UID);
        String uid = uidColumnIndex == -1 || cursor.isNull(uidColumnIndex) ? null : cursor.getString(uidColumnIndex);

        int displayNameColumnIndex = cursor.getColumnIndex(DISPLAY_NAME);
        String displayName = displayNameColumnIndex == -1 || cursor.isNull(displayNameColumnIndex) ?
                null : cursor.getString(displayNameColumnIndex);

        int entityTypeColumnIndex = cursor.getColumnIndex(ENTITY_TYPE);
        HomeEntityType entityType = entityTypeColumnIndex == -1 || cursor.isNull(entityTypeColumnIndex) ?
                null : HomeEntityType.valueOf(cursor.getString(entityTypeColumnIndex));

        return new HomeViewModel(uid, displayName, entityType);
    }

    public enum HomeEntityType {
        PROGRAM,
        TRACKED_ENTITY
    }
}
