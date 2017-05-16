package org.hisp.dhis.android.dataentry.form.dataentry;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;
import org.hisp.dhis.android.core.user.UserCredentialsModel;
import org.hisp.dhis.android.dataentry.commons.utils.CurrentDateProvider;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactory;
import org.hisp.dhis.android.dataentry.user.UserRepository;

import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
final class ProgramStageRepository implements DataEntryRepository {
    private static final String QUERY = "SELECT\n" +
            "  Field.id,\n" +
            "  Field.label,\n" +
            "  Field.type,\n" +
            "  Field.mandatory,\n" +
            "  Field.optionSet,\n" +
            "  Value.value\n" +
            "FROM Event\n" +
            "  LEFT OUTER JOIN (\n" +
            "      SELECT\n" +
            "        DataElement.displayName AS label,\n" +
            "        DataElement.valueType AS type,\n" +
            "        DataElement.uid AS id,\n" +
            "        DataElement.optionSet AS optionSet,\n" +
            "        ProgramStageDataElement.sortOrder AS formOrder,\n" +
            "        ProgramStageDataElement.programStage AS stage,\n" +
            "        ProgramStageDataElement.compulsory AS mandatory\n" +
            "      FROM ProgramStageDataElement\n" +
            "        INNER JOIN DataElement ON DataElement.uid = ProgramStageDataElement.dataElement\n" +
            "    ) AS Field ON (Field.stage = Event.programStage)\n" +
            "  LEFT OUTER JOIN TrackedEntityDataValue AS Value ON (\n" +
            "    Value.event = Event.uid AND Value.dataElement = Field.id\n" +
            "  )\n" +
            "WHERE Event.uid = ?\n" +
            "ORDER BY Field.formOrder ASC;";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final FieldViewModelFactory fieldFactory;

    @NonNull
    private final Flowable<UserCredentialsModel> userCredentials;

    @NonNull
    private final CurrentDateProvider currentDateProvider;

    @NonNull
    private final String eventUid;

    ProgramStageRepository(@NonNull BriteDatabase briteDatabase,
            @NonNull UserRepository userRepository,
            @NonNull FieldViewModelFactory fieldFactory,
            @NonNull CurrentDateProvider currentDateProvider,
            @NonNull String eventUid) {
        this.briteDatabase = briteDatabase;
        this.fieldFactory = fieldFactory;
        this.currentDateProvider = currentDateProvider;
        this.eventUid = eventUid;

        // we want to re-use results of the user credentials query
        this.userCredentials = userRepository.credentials()
                .cacheWithInitialCapacity(1);
    }

    @NonNull
    @Override
    public Flowable<Long> save(@NonNull String uid, @Nullable String value) {
        return userCredentials.switchMap((userCredentials) -> {
            long updated = update(uid, value);
            if (updated > 0) {
                return Flowable.just(updated);
            }

            return Flowable.just(insert(uid, value, userCredentials.username()));
        });
    }

    @NonNull
    @Override
    public Flowable<List<FieldViewModel>> list() {
        return toV2Flowable(briteDatabase
                .createQuery(TrackedEntityDataValueModel.TABLE, QUERY, eventUid)
                .mapToList(this::transform));
    }

    @NonNull
    private FieldViewModel transform(@NonNull Cursor cursor) {
        return fieldFactory.create(cursor.getString(0), cursor.getString(1),
                ValueType.valueOf(cursor.getString(2)), cursor.getInt(3) == 1,
                cursor.getString(4), cursor.getString(5));
    }

    private long update(@NonNull String uid, @Nullable String value) {
        ContentValues dataValue = new ContentValues();

        // update time stamp
        dataValue.put(TrackedEntityDataValueModel.Columns.LAST_UPDATED,
                BaseIdentifiableObject.DATE_FORMAT.format(currentDateProvider.currentDate()));
        if (value == null) {
            dataValue.putNull(TrackedEntityDataValueModel.Columns.VALUE);
        } else {
            dataValue.put(TrackedEntityDataValueModel.Columns.VALUE, value);
        }

        // ToDo: write test cases for different events
        return (long) briteDatabase.update(TrackedEntityDataValueModel.TABLE, dataValue,
                TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " = ? AND " +
                        TrackedEntityDataValueModel.Columns.EVENT + " = ?", uid, eventUid);
    }

    private long insert(@NonNull String uid, @Nullable String value, @NonNull String storedBy) {
        Date created = currentDateProvider.currentDate();
        TrackedEntityDataValueModel dataValueModel =
                TrackedEntityDataValueModel.builder()
                        .created(created)
                        .lastUpdated(created)
                        .dataElement(uid)
                        .event(eventUid)
                        .value(value)
                        .storedBy(storedBy)
                        .build();
        return briteDatabase.insert(TrackedEntityDataValueModel.TABLE,
                dataValueModel.toContentValues());
    }
}
