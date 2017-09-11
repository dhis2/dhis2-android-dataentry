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
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;
import static org.hisp.dhis.android.dataentry.commons.utils.DbUtils.escapeSqlToken;

final class ProgramRepositoryImpl implements SelectionRepository {
    // Using a List of table names instead of a single one, such that the Brite
    // database knows to renderSearchResults us on change of either.
    private static final List<String> TABLES = Collections.unmodifiableList(Arrays.asList(
            ProgramModel.TABLE, OrganisationUnitProgramLinkModel.TABLE, OrganisationUnitModel.TABLE));
    private static final String STATEMENT = "SELECT DISTINCT " + Columns.UID + ", " + Columns.DISPLAY_NAME +
            " FROM " + OrganisationUnitProgramLinkModel.TABLE +
            " INNER JOIN " + ProgramModel.TABLE +
            " WHERE " + OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT + " = '%s'" +
            " AND + " + Columns.DISPLAY_NAME + " LIKE '%%%s%%'" +
            " AND " + Columns.REGISTRATION + "=? ;";

    private final BriteDatabase database;
    private final String parentUid;
    private final String registration;

    ProgramRepositoryImpl(@NonNull BriteDatabase database, @NonNull String parentUid, int registration) {
        this.database = database;
        this.parentUid = parentUid;
        this.registration = Integer.toString(registration);
    }

    @NonNull
    @Override
    public Flowable<List<SelectionViewModel>> search(@NonNull String query) {
        return toV2Flowable(database.createQuery(TABLES, String.format(Locale.US,
                STATEMENT, parentUid, escapeSqlToken(query)), registration)
                .mapToList(cursor -> SelectionViewModel.create(
                        cursor.getString(0), cursor.getString(1))));
    }
}
