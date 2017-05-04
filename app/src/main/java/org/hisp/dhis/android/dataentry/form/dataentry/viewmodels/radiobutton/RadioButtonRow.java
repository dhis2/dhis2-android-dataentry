package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.radiobutton;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.Row;

import io.reactivex.observers.DisposableObserver;

final public class RadioButtonRow implements Row<RadioButtonViewHolder, RadioButtonViewModel> {

    @Override
    public RadioButtonViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new RadioButtonViewHolder(inflater.inflate(R.layout.recyclerview_row_radiobutton, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RadioButtonViewHolder viewHolder, @NonNull RadioButtonViewModel viewModel) {
        viewHolder.update(viewModel);
    }
}