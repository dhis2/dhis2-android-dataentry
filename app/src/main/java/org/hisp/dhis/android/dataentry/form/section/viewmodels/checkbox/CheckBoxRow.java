package org.hisp.dhis.android.dataentry.form.section.viewmodels.checkbox;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.Row;

import io.reactivex.observers.DisposableObserver;

public class CheckBoxRow implements Row<CheckBoxViewHolder, CheckBoxViewModel> {

    public CheckBoxRow() {
        // explicit empty constructor
    }

    @Override
    public CheckBoxViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new CheckBoxViewHolder(inflater.inflate(
                R.layout.recyclerview_row_checkbox, parent, false));
    }

    @Override
    public void onBindViewHolder(CheckBoxViewHolder viewHolder, CheckBoxViewModel viewModel,
                                 DisposableObserver<Pair<String, String>> onValueChangeObserver) {
        viewHolder.update(viewModel, onValueChangeObserver);
    }
}