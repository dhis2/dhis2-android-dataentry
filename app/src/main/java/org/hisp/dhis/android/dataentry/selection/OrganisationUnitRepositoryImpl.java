package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel.Columns;

import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

public class OrganisationUnitRepositoryImpl implements SelectionRepository {

    public static final String STATEMENT = "SELECT " + Columns.UID + ", " + Columns.DISPLAY_NAME +
            " FROM " + OrganisationUnitModel.TABLE;

    BriteDatabase database;

    public OrganisationUnitRepositoryImpl(BriteDatabase database) {
        this.database = database;
    }

    @NonNull
    @Override
    public Flowable<List<SelectionViewModel>> list() { //uid is meaningless here.
        return toV2Flowable(database.createQuery(OrganisationUnitModel.TABLE, STATEMENT)
                .mapToList(cursor -> SelectionViewModel.from(cursor, Columns.UID, Columns.DISPLAY_NAME))
        );
    }
}
