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

public class OptionSetRepositoryImpl implements SelectionRepository {

    //TODO: consider abstracting these for the entire database in a separate class that can be used to querry.
    // magic: need to have foreign keys of the current table referenced to get sqlBright updates.
    private static List<String> OPTIONS_TABLES = Collections.unmodifiableList(
            Arrays.asList(OptionSetModel.TABLE, OptionModel.TABLE)
    );

    private static final String SELECT_OPTIONS = "SELECT " +
            OptionModel.Columns.UID + ", " + OptionModel.Columns.DISPLAY_NAME +
            " FROM " + OptionModel.TABLE +
            " WHERE " + OptionModel.Columns.OPTION_SET + " = ?;";

    private final BriteDatabase database;

    public OptionSetRepositoryImpl(@NonNull BriteDatabase database) {
        this.database = database;
    }

    @Override
    public Flowable<List<SelectionViewModel>> list(@NonNull String uid) {
        return toV2Flowable(database.createQuery(OPTIONS_TABLES, SELECT_OPTIONS, uid)
                .mapToList(cursor -> SelectionViewModel.from(cursor, OptionModel.Columns.UID,
                        OptionModel.Columns.DISPLAY_NAME)
                )
        );
    }
}