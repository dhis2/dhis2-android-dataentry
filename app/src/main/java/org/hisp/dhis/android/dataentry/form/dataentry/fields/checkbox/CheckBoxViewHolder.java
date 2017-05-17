package org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.utils.Preconditions;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.FlowableProcessor;
import rx.exceptions.OnErrorNotImplementedException;

final class CheckBoxViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.checkbox_row_checkbox)
    CheckBox checkBox;

    @BindView(R.id.textview_row_label)
    TextView textViewLabel;

    @NonNull
    BehaviorProcessor<CheckBoxViewModel> model;

    @SuppressWarnings("CheckReturnValue")
    CheckBoxViewHolder(@NonNull ViewGroup parent, @NonNull View itemView,
            @NonNull FlowableProcessor<RowAction> processor) {
        super(itemView);

        model = BehaviorProcessor.create();
        model.subscribe(checkBoxViewModel -> {
            textViewLabel.setText(checkBoxViewModel.label());
            checkBox.setChecked(CheckBoxViewModel.Value.CHECKED
                    .equals(checkBoxViewModel.value()));
        });

        ButterKnife.bind(this, itemView);
        RxCompoundButton.checkedChanges(checkBox)
                .takeUntil(RxView.detaches(parent))
                .filter(isChecked -> model.hasValue())
                .filter(isChecked -> !Preconditions.equals(
                        model.getValue().value(), mapValue(isChecked)))
                .map(isChecked -> RowAction.create(model.getValue().uid(),
                        String.valueOf(mapValue(isChecked))))
                .subscribe(action -> processor.onNext(action), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                });
    }

    @NonNull
    CheckBoxViewModel.Value mapValue(@NonNull Boolean isChecked) {
        return isChecked ? CheckBoxViewModel.Value.CHECKED : CheckBoxViewModel.Value.UNCHECKED;
    }

    void update(@NonNull CheckBoxViewModel checkBoxViewModel) {
        model.onNext(checkBoxViewModel);
    }
}