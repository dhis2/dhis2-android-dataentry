package org.hisp.dhis.android.dataentry.form.dataentry;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;

import io.reactivex.Flowable;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class AttributeValueStore implements DataEntryStore {
    private static final String UPDATE = "UPDATE TrackedEntityAttributeValue\n" +
            "SET lastUpdated = ?, value = ?\n" +
            "WHERE trackedEntityInstance = (\n" +
            "  SELECT trackedEntityInstance FROM Enrollment WHERE Enrollment.uid = ? LIMIT 1\n" +
            ") AND trackedEntityAttribute = ?;";

    private static final String INSERT = "INSERT INTO TrackedEntityAttributeValue ( " +
            "created, lastUpdated, value, trackedEntityAttribute, trackedEntityInstance" +
            ") VALUES (?, ?, ?, ?, (\n" +
            "  SELECT trackedEntityInstance FROM Enrollment WHERE uid = ? LIMIT 1\n" +
            "));";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final SQLiteStatement updateStatement;

    @NonNull
    private final SQLiteStatement insertStatement;

    @NonNull
    private final CurrentDateProvider currentDateProvider;

    @NonNull
    private final String enrollment;

    AttributeValueStore(@NonNull BriteDatabase briteDatabase,
            @NonNull CurrentDateProvider currentDateProvider, @NonNull String enrollment) {
        this.enrollment = enrollment;
        this.briteDatabase = briteDatabase;
        this.currentDateProvider = currentDateProvider;

        updateStatement = briteDatabase.getWritableDatabase()
                .compileStatement(UPDATE);
        insertStatement = briteDatabase.getWritableDatabase()
                .compileStatement(INSERT);
    }

    @NonNull
    @Override
    public Flowable<Long> save(@NonNull String uid, @Nullable String value) {
        return Flowable.defer(() -> {
            long updated = update(uid, value);
            if (updated > 0) {
                return Flowable.just(updated);
            }

            return Flowable.just(insert(uid, value));
        });
    }


    private long update(@NonNull String attribute, @Nullable String value) {
        sqLiteBind(updateStatement, 1, BaseIdentifiableObject.DATE_FORMAT
                .format(currentDateProvider.currentDate()));
        sqLiteBind(updateStatement, 2, value);
        sqLiteBind(updateStatement, 3, enrollment);
        sqLiteBind(updateStatement, 4, attribute);

        long updated = briteDatabase.executeUpdateDelete(
                TrackedEntityAttributeValueModel.TABLE, updateStatement);
        updateStatement.clearBindings();

        return updated;
    }

    private long insert(@NonNull String attribute, @NonNull String value) {
        String created = BaseIdentifiableObject.DATE_FORMAT
                .format(currentDateProvider.currentDate());

        sqLiteBind(insertStatement, 1, created);
        sqLiteBind(insertStatement, 2, created);
        sqLiteBind(insertStatement, 3, value);
        sqLiteBind(insertStatement, 4, attribute);
        sqLiteBind(insertStatement, 5, enrollment);

        long inserted = briteDatabase.executeInsert(
                TrackedEntityAttributeValueModel.TABLE, insertStatement);
        insertStatement.clearBindings();

        return inserted;
    }
}
