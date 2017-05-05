package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.radiobutton;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxRadioGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.RowAction;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.processors.FlowableProcessor;
import rx.exceptions.OnErrorNotImplementedException;

final class RadioButtonViewHolder extends RecyclerView.ViewHolder {
    private static final String EMPTY_FIELD = "";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    // Views
    @BindView(R.id.textview_row_label)
    TextView label;

    @BindView(R.id.radiogroup_radiobutton_row)
    RadioGroup radioGroup;

    @BindView(R.id.radiobutton_row_radiobutton_first)
    RadioButton firstRadioButton;

    @BindView(R.id.radiobutton_row_radiobutton_second)
    RadioButton secondRadioButton;

    private RadioButtonViewModel viewModel;

    @SuppressWarnings("CheckReturnValue")
    RadioButtonViewHolder(@NonNull ViewGroup parent, @NonNull View itemView,
            @NonNull FlowableProcessor<RowAction> processor) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        RxRadioGroup.checkedChanges(radioGroup)
                .skipInitialValue()
                .takeUntil(RxView.detaches(parent))
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(this::rowAction)
                .subscribe(rowAction -> processor.onNext(rowAction), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                });
    }

    void update(@NonNull RadioButtonViewModel model) {
        viewModel = model;
        label.setText(viewModel.label());

        if (viewModel.value() == null) {
            // value is null: no radio button should be checked
            radioGroup.clearCheck();
        } else if (viewModel.value()) {
            firstRadioButton.setChecked(true);
            secondRadioButton.setChecked(false);
        } else {
            secondRadioButton.setChecked(true);
            firstRadioButton.setChecked(false);
        }
    }

    @NonNull
    private RowAction rowAction(@NonNull Integer checkedId) {
        if (checkedId == R.id.radiobutton_row_radiobutton_first) {
            return RowAction.create(viewModel.uid(), TRUE);
        } else if (checkedId == R.id.radiobutton_row_radiobutton_second) {
            return RowAction.create(viewModel.uid(), FALSE);
        }

        return RowAction.create(viewModel.uid(), EMPTY_FIELD);
    }
}
