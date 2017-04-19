package org.hisp.dhis.android.dataentry.form.section.viewmodels.radiobutton;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.Row;

import io.reactivex.observers.DisposableObserver;

final public class RadioButtonRow implements Row<RadioButtonViewHolder, RadioButtonViewModel> {

    @Override
    public RadioButtonViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new RadioButtonViewHolder(inflater.inflate(R.layout.recyclerview_row_radiobutton, parent, false));
    }

    @Override
    public void onBindViewHolder(RadioButtonViewHolder viewHolder, RadioButtonViewModel viewModel,
                                 DisposableObserver<Pair<String, String>> onValueChangeObserver) {
        viewHolder.update(viewModel, onValueChangeObserver);
    }
}