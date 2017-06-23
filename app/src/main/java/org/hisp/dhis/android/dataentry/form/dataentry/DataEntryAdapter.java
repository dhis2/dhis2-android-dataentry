package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.form.dataentry.fields.FieldViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.Row;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox.CheckBoxRow;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox.CheckBoxViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.daterow.DateRow;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.daterow.DateViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextRow;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.optionsrow.OptionsRow;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.optionsrow.OptionsViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.radiobutton.RadioButtonRow;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.radiobutton.RadioButtonViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.text.TextRow;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.text.TextViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

final class DataEntryAdapter extends Adapter {
    private static final int ROW_CHECKBOX = 0;
    private static final int ROW_EDITTEXT = 1;
    private static final int ROW_RADIO_BUTTONS = 2;
    private static final int ROW_TEXT = 3;
    private static final int ROW_OPTIONS = 4;
    private static final int ROW_DATE = 5;

    @NonNull
    private final List<FieldViewModel> viewModels;

    @NonNull
    private final FlowableProcessor<RowAction> processor;

    @NonNull
    private final List<Row> rows;

    DataEntryAdapter(@NonNull LayoutInflater layoutInflater,
            @NonNull FragmentManager fragmentManager,
            @NonNull DataEntryArguments dataEntryArguments) {
        rows = new ArrayList<>();
        viewModels = new ArrayList<>();
        processor = PublishProcessor.create();

        rows.add(ROW_CHECKBOX, new CheckBoxRow(layoutInflater, processor));
        rows.add(ROW_EDITTEXT, new EditTextRow(layoutInflater, processor));
        rows.add(ROW_RADIO_BUTTONS, new RadioButtonRow(layoutInflater, processor));
        rows.add(ROW_TEXT, new TextRow(layoutInflater));
        rows.add(ROW_OPTIONS, new OptionsRow(layoutInflater, fragmentManager,
                processor, dataEntryArguments));
        rows.add(ROW_DATE, new DateRow(layoutInflater, fragmentManager, processor));
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return rows.get(viewType).onCreate(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        rows.get(holder.getItemViewType()).onBind(holder,
                viewModels.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        FieldViewModel viewModel = viewModels.get(position);
        if (viewModel instanceof CheckBoxViewModel) {
            return ROW_CHECKBOX;
        } else if (viewModel instanceof EditTextModel) {
            return ROW_EDITTEXT;
        } else if (viewModel instanceof RadioButtonViewModel) {
            return ROW_RADIO_BUTTONS;
        } else if (viewModel instanceof TextViewModel) {
            return ROW_TEXT;
        } else if (viewModel instanceof OptionsViewModel) {
            return ROW_OPTIONS;
        } else if (viewModel instanceof DateViewModel) {
            return ROW_DATE;
        } else {
            throw new IllegalStateException("Unsupported view model type: "
                    + viewModel.getClass());
        }
    }

    @Override
    public long getItemId(int position) {
        return viewModels.get(position).uid().hashCode();
    }

    @NonNull
    FlowableProcessor<RowAction> asFlowable() {
        return processor;
    }

    void swap(@NonNull List<FieldViewModel> updates) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new DataEntryDiffCallback(viewModels, updates));

        viewModels.clear();
        viewModels.addAll(updates);

        diffResult.dispatchUpdatesTo(this);
    }
}
