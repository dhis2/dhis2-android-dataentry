package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel.Columns;
import org.hisp.dhis.android.dataentry.commons.utils.DbUtils;

import java.util.List;
import java.util.Locale;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

public final class OrganisationUnitRepositoryImpl implements SelectionRepository {
    private static final String STATEMENT = "SELECT " + Columns.UID + ", " + Columns.DISPLAY_NAME +
            " FROM " + OrganisationUnitModel.TABLE +
            " WHERE " + Columns.DISPLAY_NAME + " LIKE '%%%s%%';";

    private final BriteDatabase database;

    public OrganisationUnitRepositoryImpl(BriteDatabase database) {
        this.database = database;
    }

    @NonNull
    @Override
    public Flowable<List<SelectionViewModel>> search(@NonNull String query) {
        return toV2Flowable(database.createQuery(OrganisationUnitModel.TABLE,
                String.format(Locale.US, STATEMENT, DbUtils.escapeSqlToken(query)))
                .mapToList(cursor -> SelectionViewModel.create(
                        cursor.getString(0), cursor.getString(1))));
    }
}
