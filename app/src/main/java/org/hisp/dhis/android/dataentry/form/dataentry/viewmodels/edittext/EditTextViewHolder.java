package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.edittext;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.RowAction;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.FlowableProcessor;
import rx.exceptions.OnErrorNotImplementedException;

import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

final class EditTextViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textview_row_label)
    TextView textViewLabel;

    @BindView(R.id.edittext_row_textinputlayout)
    TextInputLayout textInputLayout;

    @BindView(R.id.edittext_row_edittext)
    EditText editText;

    private EditTextViewModel viewModel;

    @SuppressWarnings("CheckReturnValue")
    EditTextViewHolder(@NonNull ViewGroup parent, @NonNull View itemView,
            @NonNull FlowableProcessor<RowAction> processor) {
        super(itemView);

        ButterKnife.bind(this, itemView);
        RxTextView.afterTextChangeEvents(editText)
                .skipInitialValue()
                .takeUntil(RxView.detaches(parent))
                .filter(valueHasChanged())
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(event -> RowAction.create(viewModel.uid(),
                        event.editable() == null ? "" : event.editable().toString()))
                .subscribe(action -> processor.onNext(action), throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                });

        editText.setOnFocusChangeListener((v, hasFocus) -> toggleHintVisibility(hasFocus));
    }

    void update(@NonNull EditTextViewModel model) {
        viewModel = model;

        textViewLabel.setText(viewModel.label());

        toggleHintVisibility(editText.hasFocus());
        textInputLayout.setHint(viewModel.hint());

        // AutoValue does not support array of non-primitives so we need to transform immutable list to array
        editText.setFilters(viewModel.inputFilters().toArray(new InputFilter[0]));

        editText.setText(viewModel.value());
        editText.setInputType(viewModel.inputType());
        editText.setMaxLines(viewModel.maxLines());
    }

    private void toggleHintVisibility(Boolean hasFocus) {
        if (hasFocus || isEmpty(editText.getText())) {
            textInputLayout.setHintEnabled(true);
        } else {
            textInputLayout.setHintEnabled(false);
        }
    }

    @NonNull
    private Predicate<TextViewAfterTextChangeEvent> valueHasChanged() {
        return textChangeEvent -> viewModel != null &&
                !viewModel.value().equals(textChangeEvent.editable().toString());
    }
}
