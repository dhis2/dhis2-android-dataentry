package org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.Row;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;

import io.reactivex.processors.FlowableProcessor;

public final class EditTextRow implements Row {

    @NonNull
    private final LayoutInflater inflater;

    @NonNull
    private final FlowableProcessor<RowAction> processor;

    public EditTextRow(@NonNull LayoutInflater inflater,
            @NonNull FlowableProcessor<RowAction> processor) {
        this.inflater = inflater;
        this.processor = processor;
    }

    @NonNull
    @Override
    public ViewHolder onCreate(@NonNull ViewGroup parent) {
        return new EditTextViewHolder(parent, inflater.inflate(
                R.layout.recyclerview_row_edittext, parent, false), processor);
    }

    @Override
    public void onBind(@NonNull ViewHolder viewHolder, @NonNull FieldViewModel viewModel) {
        ((EditTextViewHolder) viewHolder).update((EditTextViewModel) viewModel);
    }
}