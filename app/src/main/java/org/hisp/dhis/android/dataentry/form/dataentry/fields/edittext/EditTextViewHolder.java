package org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.utils.Preconditions;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.subjects.BehaviorSubject;
import rx.exceptions.OnErrorNotImplementedException;

import static java.lang.String.valueOf;
import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

final class EditTextViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textview_row_label)
    TextView textViewLabel;

    @BindView(R.id.edittext_row_textinputlayout)
    TextInputLayout textInputLayout;

    @BindView(R.id.edittext_row_edittext)
    EditText editText;

    @NonNull
    BehaviorSubject<EditTextModel> model;

    @SuppressWarnings("CheckReturnValue")
    EditTextViewHolder(@NonNull ViewGroup parent, @NonNull View itemView,
            @NonNull FlowableProcessor<RowAction> processor) {
        super(itemView);

        // bind views
        ButterKnife.bind(this, itemView);

        // source of data for this view
        model = BehaviorSubject.create();
        model.subscribe(editTextModel -> {
            editText.setText(editTextModel.value() == null ?
                    null : valueOf(editTextModel.value()));
            editText.setInputType(editTextModel.inputType());
            editText.setMaxLines(editTextModel.maxLines());
            editText.setSelection(editText.getText() == null ? 0 : editText.getText().length());

            textViewLabel.setText(editTextModel.label());
            textInputLayout.setHint(editText.hasFocus() ||
                    isEmpty(editText.getText()) ? editTextModel.hint() : "");
        });

        // listen to changes in edit text, push them to
        // observer only in case if they are distinct
        RxTextView.afterTextChangeEvents(editText)
                .takeUntil(RxView.detaches(parent))
                .filter(event -> model.hasValue())
                .filter(event -> !Preconditions.equals(event.editable().toString(),
                        model.getValue().value() == null ? "" : valueOf(model.getValue().value())))
                .map(event -> RowAction.create(model.getValue().uid(),
                        event.editable().toString()))
                .debounce(512, TimeUnit.MILLISECONDS)
                .subscribe(processor::onNext, throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                });

        // show and hide hint depending on focus state of the edittext.
        RxView.focusChanges(editText)
                .takeUntil(RxView.detaches(parent))
                .subscribe(hasFocus -> textInputLayout.setHint((hasFocus || isEmpty(editText.getText()))
                        && model.hasValue() ? model.getValue().hint() : ""));
    }

    void update(@NonNull EditTextModel editTextModel) {
        model.onNext(editTextModel);
    }
}
