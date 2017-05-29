package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;

import org.hisp.dhis.android.core.option.OptionModel;
import org.hisp.dhis.android.core.option.OptionSetModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

import static hu.akarnokd.rxjava.interop.RxJavaInterop.toV2Flowable;

public class OptionSetRepositoryImpl implements SelectionRepository {

    PublishSubject<List<SelectionViewModel>> publishSubject;

    //TODO: consider abstracting these for the entire database in a separate class that can be used to querry.
    // magic: need to have foreign keys of the current table referenced to get sqlBright updates.
    private static List<String> OPTIONS_TABLES = Collections.unmodifiableList(
            Arrays.asList(OptionSetModel.TABLE, OptionModel.TABLE)
    );

    private static final String SELECT_OPTIONS = "SELECT " +
            OptionModel.Columns.UID + ", " + OptionModel.Columns.DISPLAY_NAME +
            " FROM " + OptionModel.TABLE +
            " WHERE " + OptionModel.Columns.OPTION_SET + " = ?;";

    private BriteDatabase database;

    public OptionSetRepositoryImpl(@NonNull BriteDatabase database) {
        this.database = database;
        publishSubject = PublishSubject.create();
    }

    @Override
    public Flowable<List<SelectionViewModel>> list(@NonNull String uid) {
        return toV2Flowable(database.createQuery(OPTIONS_TABLES, SELECT_OPTIONS, uid)
                .mapToList(cursor -> SelectionViewModel.from(cursor, OptionModel.Columns.UID,
                        OptionModel.Columns.DISPLAY_NAME)
                )
        );
    }

    /*@Override
    public Consumer<CharSequence> search() {
        return new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                Log.d("Char seq",  charSequence.toString());

                publishSubject.onNext(new ArrayList<>());

            }
        };
    }*/
}