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

final class ProgramStageRepositoryImpl implements DataEntryRepository {
    private static final String QUERY = "SELECT\n" +
            "  ProgramStageDataElement.compulsory,\n" +
            "  DataElement.displayName,\n" +
            "  DataElement.valueType,\n" +
            "  DataElement.optionSet\n" +
            "FROM Event\n" +
            "  LEFT OUTER JOIN ProgramStageDataElement ON " +
            "ProgramStageDataElement.programStage = Event.programStage\n" +
            "  LEFT OUTER JOIN DataElement ON ProgramStageDataElement.dataElement = DataElement.uid\n" +
            "WHERE Event.uid = ?\n" +
            "ORDER BY ProgramStageDataElement.sortOrder";

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
