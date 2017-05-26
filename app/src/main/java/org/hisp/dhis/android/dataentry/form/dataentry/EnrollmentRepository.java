package org.hisp.dhis.android.dataentry.form.dataentry;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactory;

import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;
import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class EnrollmentRepository implements DataEntryRepository {
    private static final String QUERY = "SELECT\n" +
            "  Field.id,\n" +
            "  Field.label,\n" +
            "  Field.type,\n" +
            "  Field.mandatory,\n" +
            "  Field.optionSet,\n" +
            "  Value.value\n" +
            "FROM (Enrollment INNER JOIN Program ON Program.uid = Enrollment.program)\n" +
            "  LEFT OUTER JOIN (\n" +
            "      SELECT\n" +
            "        TrackedEntityAttribute.uid AS id,\n" +
            "        TrackedEntityAttribute.displayName AS label,\n" +
            "        TrackedEntityAttribute.valueType AS type,\n" +
            "        TrackedEntityAttribute.optionSet AS optionSet,\n" +
            "        ProgramTrackedEntityAttribute.program AS program,\n" +
            "        ProgramTrackedEntityAttribute.sortOrder AS formOrder, \n" +
            "        ProgramTrackedEntityAttribute.mandatory AS mandatory\n" +
            "      FROM ProgramTrackedEntityAttribute INNER JOIN TrackedEntityAttribute\n" +
            "          ON TrackedEntityAttribute.uid = ProgramTrackedEntityAttribute.trackedEntityAttribute\n" +
            "    ) AS Field ON Field.program = Program.uid\n" +
            "  LEFT OUTER JOIN TrackedEntityAttributeValue AS Value ON (\n" +
            "    Value.trackedEntityAttribute = Field.id \n" +
            "        AND Value.trackedEntityInstance = Enrollment.trackedEntityInstance)\n" +
            "WHERE Enrollment.uid = ?\n" +
            "ORDER BY Field.formOrder ASC;";

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
    private final FieldViewModelFactory fieldFactory;

    @NonNull
    private final CurrentDateProvider currentDateProvider;

    @NonNull
    private final String enrollment;

    EnrollmentRepository(@NonNull BriteDatabase briteDatabase,
            @NonNull FieldViewModelFactory fieldFactory,
            @NonNull CurrentDateProvider currentDateProvider,
            @NonNull String enrollment) {
        this.briteDatabase = briteDatabase;
        this.fieldFactory = fieldFactory;
        this.currentDateProvider = currentDateProvider;
        this.enrollment = enrollment;

        this.updateStatement = briteDatabase.getWritableDatabase()
                .compileStatement(UPDATE);
        this.insertStatement = briteDatabase.getWritableDatabase()
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

    @NonNull
    @Override
    public Flowable<List<FieldViewModel>> list() {
        return toV2Flowable(briteDatabase
                .createQuery(TrackedEntityAttributeValueModel.TABLE, QUERY, enrollment)
                .mapToList(this::transform));
    }

    @NonNull
    private FieldViewModel transform(@NonNull Cursor cursor) {
        return fieldFactory.create(cursor.getString(0), cursor.getString(1),
                ValueType.valueOf(cursor.getString(2)), cursor.getInt(3) == 1,
                cursor.getString(4), cursor.getString(5));
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
