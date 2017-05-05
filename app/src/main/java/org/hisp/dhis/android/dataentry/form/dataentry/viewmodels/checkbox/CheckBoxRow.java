package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.checkbox;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.FormItemViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.Row;
import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.RowAction;

import io.reactivex.processors.FlowableProcessor;

public class CheckBoxRow implements Row {

    @NonNull
    private final LayoutInflater inflater;

    @NonNull
    private final FlowableProcessor<RowAction> processor;

    public CheckBoxRow(@NonNull LayoutInflater inflater,
            @NonNull FlowableProcessor<RowAction> processor) {
        this.inflater = inflater;
        this.processor = processor;
    }

    @NonNull
    @Override
    public ViewHolder onCreate(@NonNull ViewGroup parent) {
        return new CheckBoxViewHolder(parent, inflater.inflate(
                R.layout.recyclerview_row_checkbox, parent, false), processor);
    }

    @Override
    public void onBind(@NonNull ViewHolder viewHolder, @NonNull FormItemViewModel viewModel) {
        ((CheckBoxViewHolder) viewHolder).update((CheckBoxViewModel) viewModel);
    }
}