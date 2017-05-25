package org.hisp.dhis.android.dataentry.form.dataentry;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel.Columns;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactory;

import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

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

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final FieldViewModelFactory fieldFactory;

    @NonNull
    private final CurrentDateProvider currentDateProvider;

    @NonNull
    private final String trackedEntityInstance;

    @NonNull
    private final String enrollment;

    EnrollmentRepository(@NonNull BriteDatabase briteDatabase,
            @NonNull FieldViewModelFactory fieldFactory,
            @NonNull CurrentDateProvider currentDateProvider,
            @NonNull String trackedEntityInstance,
            @NonNull String enrollment) {
        this.briteDatabase = briteDatabase;
        this.fieldFactory = fieldFactory;
        this.currentDateProvider = currentDateProvider;
        this.trackedEntityInstance = trackedEntityInstance;
        this.enrollment = enrollment;
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

    private long update(@NonNull String uid, @Nullable String value) {
        ContentValues attributeValue = new ContentValues();

        // update time stamp
        attributeValue.put(TrackedEntityAttributeValueModel.Columns.LAST_UPDATED,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDateProvider.currentDate()));
        if (value == null) {
            attributeValue.putNull(TrackedEntityAttributeValueModel.Columns.VALUE);
        } else {
            attributeValue.put(TrackedEntityAttributeValueModel.Columns.VALUE, value);
        }

        return (long) briteDatabase.update(TrackedEntityAttributeValueModel.TABLE, attributeValue,
                Columns.TRACKED_ENTITY_ATTRIBUTE + " = ? AND " +
                        Columns.TRACKED_ENTITY_INSTANCE + " = ?", uid, trackedEntityInstance);
    }

    private long insert(@NonNull String uid, @Nullable String value) {
        Date created = currentDateProvider.currentDate();
        TrackedEntityAttributeValueModel attributeValueModel =
                TrackedEntityAttributeValueModel.builder()
                        .created(created)
                        .lastUpdated(created)
                        .trackedEntityAttribute(uid)
                        .trackedEntityInstance(trackedEntityInstance)
                        .value(value)
                        .build();
        return briteDatabase.insert(TrackedEntityAttributeValueModel.TABLE,
                attributeValueModel.toContentValues());
    }
}
