package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.edittext;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.Row;

import io.reactivex.observers.DisposableObserver;

public final class EditTextRow implements Row<EditTextViewHolder, EditTextViewModel> {

    public EditTextRow() {
        // explicit empty constructor
    }

    @Override
    public EditTextViewHolder onCreateViewHolder(
            @NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new EditTextViewHolder(inflater.inflate(
                R.layout.recyclerview_row_edittext, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EditTextViewHolder viewHolder,
            @NonNull EditTextViewModel viewModel) {
        viewHolder.update(viewModel);
    }

}