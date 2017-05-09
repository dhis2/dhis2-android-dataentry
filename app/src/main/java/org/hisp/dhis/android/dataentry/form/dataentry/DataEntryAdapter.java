package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.form.dataentry.fields.EditableTextViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.FormItemViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.Row;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox.CheckBoxRow;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox.CheckBoxViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextRow;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

final class DataEntryAdapter extends Adapter {
    private static final int ROW_CHECKBOX = 0;
    private static final int ROW_EDITTEXT = 1;
    private static final int ROW_RADIO_BUTTONS = 2;
    private static final int ROW_TEXT = 3;

    @NonNull
    private final List<FormItemViewModel> viewModels;

    @NonNull
    private final FlowableProcessor<RowAction> processor;

    @NonNull
    private final List<Row> rows;

    DataEntryAdapter(@NonNull LayoutInflater layoutInflater) {
        rows = new ArrayList<>();
        viewModels = new ArrayList<>();
        processor = PublishProcessor.create();

        rows.add(ROW_CHECKBOX, new CheckBoxRow(layoutInflater, processor));
        rows.add(ROW_EDITTEXT, new EditTextRow(layoutInflater, processor));
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return rows.get(viewType).onCreate(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        rows.get(holder.getItemViewType()).onBind(holder, viewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        FormItemViewModel viewModel = viewModels.get(position);
        if (viewModel instanceof CheckBoxViewModel) {
            return ROW_CHECKBOX;
        } else if (viewModel instanceof EditableTextViewModel) {
            return ROW_EDITTEXT;
        } else {
            return 2;
        }
    }

    @NonNull
    FlowableProcessor<RowAction> asFlowable() {
        return processor;
    }

    void swap(@Nullable List<FormItemViewModel> viewModels) {
        this.viewModels.clear();

        if (viewModels != null) {
            this.viewModels.addAll(viewModels);
        }

        notifyDataSetChanged();
    }
}
