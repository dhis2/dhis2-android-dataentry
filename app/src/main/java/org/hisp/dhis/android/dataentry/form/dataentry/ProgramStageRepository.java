package org.hisp.dhis.android.dataentry.form.dataentry;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.FormItemViewModel;

import java.util.List;

import io.reactivex.Flowable;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

final class ProgramStageRepository implements DataEntryRepository {

    @NonNull
    private final BriteDatabase briteDatabase;

    @NonNull
    private final String program;

    ProgramStageRepository(@NonNull BriteDatabase briteDatabase, @NonNull String program) {
        this.briteDatabase = briteDatabase;
        this.program = program;
    }

    @NonNull
    @Override
    public Flowable<List<FormItemViewModel>> formItems() {
        return toV2Flowable(briteDatabase.createQuery("", "")
                .mapToList(this::transform));
    }

    @NonNull
    private FormItemViewModel transform(@NonNull Cursor cursor) {
        return null;
    }
}
