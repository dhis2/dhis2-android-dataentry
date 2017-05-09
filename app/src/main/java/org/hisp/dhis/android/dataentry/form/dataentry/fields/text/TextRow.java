package org.hisp.dhis.android.dataentry.form.dataentry.fields.text;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.Row;

public final class TextRow implements Row {

    @NonNull
    private final LayoutInflater inflater;

    public TextRow(@NonNull LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public ViewHolder onCreate(@NonNull ViewGroup parent) {
        return new TextViewHolder(inflater.inflate(
                R.layout.recyclerview_row_textview, parent, false));
    }

    @Override
    public void onBind(@NonNull ViewHolder viewHolder, @NonNull FieldViewModel viewModel) {
        ((TextViewHolder) viewHolder).update((TextViewModel) viewModel);
    }
}