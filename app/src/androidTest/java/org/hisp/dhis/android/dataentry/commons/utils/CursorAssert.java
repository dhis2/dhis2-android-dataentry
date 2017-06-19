package org.hisp.dhis.android.dataentry.commons.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import static org.assertj.core.api.Java6Assertions.assertThat;

public final class CursorAssert {
    private final Cursor cursor;
    private int row;

    public static CursorAssert assertThatCursor(Cursor cursor) {
        return new CursorAssert(cursor);
    }

    private CursorAssert(Cursor cursor) {
        this.cursor = cursor;

        // set to first row by default
        this.row = 0;
    }

    @NonNull
    public CursorAssert hasRow(@NonNull Object... values) {
        assertThat(cursor.moveToNext()).isTrue();
        row = row + 1;

        assertThat(cursor.getColumnCount()).isEqualTo(values.length);
        for (int index = 0; index < values.length; index++) {
            assertThat(cursor.getString(index))
                    .isEqualTo(values[index] == null ? values[index] : String.valueOf(values[index]));
        }

        return this;
    }

    @NonNull
    public CursorAssert hasRow(@NonNull String[] projection, @NonNull ContentValues contentValues) {
        assertThat(projection.length)
                .isEqualTo(contentValues.size());

        Object[] values = new Object[projection.length];
        for (int index = 0; index < projection.length; index++) {
            values[index] = contentValues.get(projection[index]);
        }

        hasRow(values);

        return this;
    }

    public void isExhausted() {
        if (cursor.moveToNext()) {
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                if (i > 0) {
                    data.append(", ");
                }

                data.append(cursor.getString(i));
            }

            throw new AssertionError("Expected no more rows but was: " + data);
        }

        cursor.close();
    }
}