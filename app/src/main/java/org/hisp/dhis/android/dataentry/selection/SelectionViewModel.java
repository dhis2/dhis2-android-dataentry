package org.hisp.dhis.android.dataentry.selection;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SelectionViewModel {

    @NonNull
    public abstract String uid();

    @NonNull
    public abstract String label();

    public static SelectionViewModel from(Cursor cursor, String uidColumn, String nameColumn) {
        String uid = cursor.getString(cursor.getColumnIndex(uidColumn));
        String name = cursor.getString(cursor.getColumnIndex(nameColumn));

        return new AutoValue_SelectionViewModel(uid, name);
    }

    public static SelectionViewModel create(String uid, String name) {
        return new AutoValue_SelectionViewModel(uid, name);
    }
}
