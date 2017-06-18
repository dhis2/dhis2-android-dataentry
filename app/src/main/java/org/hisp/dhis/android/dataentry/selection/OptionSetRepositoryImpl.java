package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.option.OptionModel;
import org.hisp.dhis.android.core.option.OptionSetModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

final class OptionSetRepositoryImpl implements SelectionRepository {
    // magic: need to have foreign keys of the current table referenced to get sqlBright updates.
    private static List<String> OPTIONS_TABLES = Collections.unmodifiableList(
            Arrays.asList(OptionSetModel.TABLE, OptionModel.TABLE)
    );

    private static final String SELECT_OPTIONS = "SELECT " +
            OptionModel.Columns.UID + ", " + OptionModel.Columns.DISPLAY_NAME +
            " FROM " + OptionModel.TABLE +
            " WHERE " + OptionModel.Columns.OPTION_SET + " = ?;";

    private final BriteDatabase database;
    private final String uid;

    OptionSetRepositoryImpl(@NonNull BriteDatabase database, @NonNull String uid) {
        this.database = database;
        this.uid = uid;
    }

    @NonNull
    @Override
    public Flowable<List<SelectionViewModel>> list() {
        return toV2Flowable(database.createQuery(OPTIONS_TABLES, SELECT_OPTIONS, uid)
                .mapToList(cursor -> SelectionViewModel.create(
                        cursor.getString(0), cursor.getString(1))));
    }
}