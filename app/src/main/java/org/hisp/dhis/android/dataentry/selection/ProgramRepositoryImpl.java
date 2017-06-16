package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramModel.Columns;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import timber.log.Timber;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

public class ProgramRepositoryImpl implements SelectionRepository {
    //Using a List of table names instead of a single one, such that the Brite database knows to update us on change
    // of either.
    private static final List<String> TABLES = Collections.unmodifiableList(
            Arrays.asList(ProgramModel.TABLE, OrganisationUnitProgramLinkModel.TABLE,
                    OrganisationUnitModel.TABLE));

    private static final String STATEMENT = "SELECT DISTINCT " + Columns.UID + ", " + Columns.DISPLAY_NAME + " FROM " +
            OrganisationUnitProgramLinkModel.TABLE +
            " INNER JOIN " +
            ProgramModel.TABLE +
            " WHERE " + OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT + " =?;";

    private final BriteDatabase database;

    private final String parentUid;

    public ProgramRepositoryImpl(@NonNull BriteDatabase database, @NonNull String parentUid) {
        this.database = database;
        this.parentUid = parentUid;
    }

    @NonNull
    @Override
    public Flowable<List<SelectionViewModel>> list() {
        return toV2Flowable(database.createQuery(TABLES, STATEMENT, parentUid)
                .mapToList(cursor -> SelectionViewModel.from(cursor, Columns.UID, Columns.DISPLAY_NAME))
        );
    }

}
