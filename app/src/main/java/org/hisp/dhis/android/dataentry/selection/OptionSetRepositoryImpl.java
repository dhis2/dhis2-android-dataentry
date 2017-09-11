package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.option.OptionModel;
import org.hisp.dhis.android.core.option.OptionSetModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;
import static org.hisp.dhis.android.dataentry.commons.utils.DbUtils.escapeSqlToken;

final class OptionSetRepositoryImpl implements SelectionRepository {
    // magic: need to have foreign keys of the current table referenced to get sqlBright updates.
    private static List<String> OPTIONS_TABLES = Collections.unmodifiableList(
            Arrays.asList(OptionSetModel.TABLE, OptionModel.TABLE)
    );

    private static final String SELECT_OPTIONS = "SELECT " +
            OptionModel.Columns.UID + ", " +
            OptionModel.Columns.DISPLAY_NAME + ", " +
            OptionModel.Columns.CODE +
            " FROM " + OptionModel.TABLE +
            " WHERE " + OptionModel.Columns.OPTION_SET + " = '%s'" +
            " AND " + OptionModel.Columns.DISPLAY_NAME + " LIKE '%%%s%%';";

    private final BriteDatabase database;
    private final String uid;

    OptionSetRepositoryImpl(@NonNull BriteDatabase database, @NonNull String uid) {
        this.database = database;
        this.uid = uid;
    }

    @NonNull
    @Override
    public Flowable<List<SelectionViewModel>> search(@NonNull String query) {
        return toV2Flowable(database.createQuery(OPTIONS_TABLES,
                String.format(Locale.US, SELECT_OPTIONS, uid, escapeSqlToken(query)))
                .mapToList(cursor -> SelectionViewModel.create(
                        cursor.getString(0), cursor.getString(1), cursor.getString(2))));
    }
}