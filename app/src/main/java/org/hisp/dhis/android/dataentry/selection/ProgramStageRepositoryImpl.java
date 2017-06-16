package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

public class ProgramStageRepositoryImpl implements SelectionRepository {
    //Using a List of table names instead of a single one, such that the Brite database knows to update us on change
    // of either.
    private static final List<String> TABLES = Collections.unmodifiableList(
            Arrays.asList(ProgramModel.TABLE, ProgramStageModel.TABLE));

    private static final String STATEMENT = "SELECT " + ProgramStageModel.Columns.UID + ", " +
            ProgramStageModel.Columns.DISPLAY_NAME +
            " FROM " + ProgramStageModel.TABLE +
            " WHERE " + ProgramStageModel.Columns.PROGRAM + " =?;";

    private final BriteDatabase database;

    private final String parentUid;

    public ProgramStageRepositoryImpl(@NonNull BriteDatabase database, @NonNull String parentUid) {
        this.database = database;
        this.parentUid = parentUid;
    }

    @NonNull
    @Override
    public Flowable<List<SelectionViewModel>> list() {
        return toV2Flowable(database.createQuery(TABLES, STATEMENT, parentUid)
                .mapToList(cursor -> SelectionViewModel.from(cursor, ProgramStageModel.Columns.UID,
                        ProgramStageModel.Columns.DISPLAY_NAME))
        );
    }

}
