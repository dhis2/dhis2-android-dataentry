package org.hisp.dhis.android.dataentry.form.dataentry;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModelFactory;

import java.util.List;
import java.util.Locale;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;
import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
final class ProgramStageRepository implements DataEntryRepository {
    private static final String QUERY = "SELECT " +
            "  Field.id, " +
            "  Field.label, " +
            "  Field.type, " +
            "  Field.mandatory, " +
            "  Field.optionSet, " +
            "  Value.value " +
            "FROM Event " +
            "  LEFT OUTER JOIN ( " +
            "      SELECT " +
            "        DataElement.displayName AS label, " +
            "        DataElement.valueType AS type, " +
            "        DataElement.uid AS id, " +
            "        DataElement.optionSet AS optionSet, " +
            "        ProgramStageDataElement.sortOrder AS formOrder, " +
            "        ProgramStageDataElement.programStage AS stage, " +
            "        ProgramStageDataElement.compulsory AS mandatory, " +
            "        ProgramStageDataElement.programStageSection AS section " +
            "      FROM ProgramStageDataElement " +
            "        INNER JOIN DataElement ON DataElement.uid = ProgramStageDataElement.dataElement " +
            "    ) AS Field ON (Field.stage = Event.programStage) " +
            "  LEFT OUTER JOIN TrackedEntityDataValue AS Value ON ( " +
            "    Value.event = Event.uid AND Value.dataElement = Field.id " +
            "  ) " +
            " %s  " +
            "ORDER BY Field.formOrder ASC;";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final FieldViewModelFactory fieldFactory;

    @NonNull
    private final String eventUid;

    @Nullable
    private final String sectionUid;

    ProgramStageRepository(@NonNull BriteDatabase briteDatabase,
            @NonNull FieldViewModelFactory fieldFactory,
            @NonNull String eventUid, @Nullable String sectionUid) {
        this.briteDatabase = briteDatabase;
        this.fieldFactory = fieldFactory;
        this.eventUid = eventUid;
        this.sectionUid = sectionUid;
    }

    @NonNull
    @Override
    public Flowable<List<FieldViewModel>> list() {
        return toV2Flowable(briteDatabase
                .createQuery(TrackedEntityDataValueModel.TABLE, prepareStatement())
                .mapToList(this::transform));
    }

    @NonNull
    private FieldViewModel transform(@NonNull Cursor cursor) {
        return fieldFactory.create(cursor.getString(0), cursor.getString(1),
                ValueType.valueOf(cursor.getString(2)), cursor.getInt(3) == 1,
                cursor.getString(4), cursor.getString(5));
    }

    @NonNull
    @SuppressFBWarnings("VA_FORMAT_STRING_USES_NEWLINE")
    private String prepareStatement() {
        String where;
        if (isEmpty(sectionUid)) {
            where = String.format(Locale.US, "WHERE Event.uid = '%s'", eventUid);
        } else {
            where = String.format(Locale.US, "WHERE Event.uid = '%s' AND " +
                    "Field.section = '%s'", eventUid, sectionUid);
        }

        return String.format(Locale.US, QUERY, where);
    }
}
