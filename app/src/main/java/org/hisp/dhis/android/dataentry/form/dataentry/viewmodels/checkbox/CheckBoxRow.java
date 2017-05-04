package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.checkbox;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.Row;

import io.reactivex.processors.FlowableProcessor;

public class CheckBoxRow implements Row<CheckBoxViewHolder, CheckBoxViewModel> {

    @NonNull
    private final FlowableProcessor<RowAction> processor;

    public CheckBoxRow(@NonNull FlowableProcessor<RowAction> processor) {
        this.processor = processor;
    }

    @NonNull
    @Override
    public CheckBoxViewHolder onCreateViewHolder(
            @NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new CheckBoxViewHolder(parent, inflater.inflate(
                R.layout.recyclerview_row_checkbox, parent, false), processor);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckBoxViewHolder viewHolder,
            @NonNull CheckBoxViewModel viewModel) {
        viewHolder.update(viewModel);
    }
}