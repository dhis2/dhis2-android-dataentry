package org.hisp.dhis.android.dataentry.form.section.viewmodels.edittext;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.Row;

import io.reactivex.observers.DisposableObserver;

public final class EditTextRow implements Row<EditTextViewHolder, EditTextViewModel> {

    public EditTextRow() {
        // explicit empty constructor
    }

    @Override
    public EditTextViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new EditTextViewHolder(inflater.inflate(
                R.layout.recyclerview_row_edittext, parent, false));
    }

    @Override
    public void onBindViewHolder(EditTextViewHolder viewHolder, EditTextViewModel viewModel,
                                 DisposableObserver<Pair<String, String>> onValueChangeObserver) {
        viewHolder.update(viewModel, onValueChangeObserver);
    }

}