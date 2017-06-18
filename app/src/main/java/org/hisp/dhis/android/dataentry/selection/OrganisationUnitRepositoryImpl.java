package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel.Columns;

import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

final class OrganisationUnitRepositoryImpl implements SelectionRepository {
    private static final String STATEMENT = "SELECT " + Columns.UID + ", " + Columns.DISPLAY_NAME +
            " FROM " + OrganisationUnitModel.TABLE;

    private final BriteDatabase database;

    OrganisationUnitRepositoryImpl(BriteDatabase database) {
        this.database = database;
    }

    @NonNull
    @Override
    public Flowable<List<SelectionViewModel>> list() { //uid is meaningless here.
        return toV2Flowable(database.createQuery(OrganisationUnitModel.TABLE, STATEMENT)
                .mapToList(cursor -> SelectionViewModel.create(
                        cursor.getString(0), cursor.getString(1))));
    }
}
