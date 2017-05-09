package org.hisp.dhis.android.dataentry.form.dataentry;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.FormItemViewModel;

import java.util.List;

import io.reactivex.Flowable;
import timber.log.Timber;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals"
})
final class ProgramStageRepositoryImpl implements DataEntryRepository {
    private static final String QUERY = "SELECT\n" +
            "  Value._id,\n" +
            "  Field.type,\n" +
            "  Field.label,\n" +
            "  Field.optionSet,\n" +
            "  Field.mandatory,\n" +
            "  Value.value\n" +
            "FROM Event\n" +
            "  LEFT OUTER JOIN (\n" +
            "      SELECT\n" +
            "        DataElement.displayName AS label,\n" +
            "        DataElement.valueType AS type,\n" +
            "        DataElement.uid AS dataElementUid,\n" +
            "        DataElement.optionSet AS optionSet,\n" +
            "        ProgramStageDataElement.sortOrder AS formOrder,\n" +
            "        ProgramStageDataElement.programStage AS stage,\n" +
            "        ProgramStageDataElement.compulsory AS mandatory\n" +
            "      FROM ProgramStageDataElement\n" +
            "        INNER JOIN DataElement ON DataElement.uid = ProgramStageDataElement.dataElement\n" +
            "    ) AS Field ON (Field.stage = Event.programStage)\n" +
            "  LEFT OUTER JOIN TrackedEntityDataValue AS Value ON (\n" +
            "    Value.event = Event.uid AND Value.dataElement = Field.dataElementUid\n" +
            "  )\n" +
            "WHERE Event.uid = \"event_uid\"\n" +
            "ORDER BY Field.formOrder ASC;";

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final String event;

    ProgramStageRepositoryImpl(@NonNull BriteDatabase briteDatabase, @NonNull String event) {
        this.briteDatabase = briteDatabase;
        this.event = event;
    }

    @NonNull
    @Override
    public Flowable<List<FormItemViewModel>> fields() {
        return toV2Flowable(briteDatabase.createQuery(EventModel.TABLE, QUERY, event)
                .mapToList(this::transform));
    }

    @NonNull
    private FormItemViewModel transform(@NonNull Cursor cursor) {

        Timber.d("DataElement = {%s}", cursor.getString(1));
        return new FormItemViewModel() {
            @NonNull
            @Override
            public String uid() {
                return null;
            }

            @NonNull
            @Override
            public String label() {
                return null;
            }
        };
    }
}
