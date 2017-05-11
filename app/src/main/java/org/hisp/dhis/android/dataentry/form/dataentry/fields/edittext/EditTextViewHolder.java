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
import org.hisp.dhis.android.dataentry.form.dataentry.fields.RowAction;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.subjects.PublishSubject;
import rx.exceptions.OnErrorNotImplementedException;

import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

final class EditTextViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textview_row_label)
    TextView textViewLabel;

    @BindView(R.id.edittext_row_textinputlayout)
    TextInputLayout textInputLayout;

    @BindView(R.id.edittext_row_edittext)
    EditText editText;

    @NonNull
    PublishSubject<EditTextModel> modelSubject;

    @SuppressWarnings("CheckReturnValue")
    EditTextViewHolder(@NonNull ViewGroup parent, @NonNull View itemView,
            @NonNull FlowableProcessor<RowAction> processor) {
        super(itemView);

        // bind views
        ButterKnife.bind(this, itemView);

        // source of data for this view
        modelSubject = PublishSubject.create();
        modelSubject.subscribe(model -> {
            if (model.value() != null) {
                editText.setText(String.valueOf(model.value()));
            }

            editText.setInputType(model.inputType());
            editText.setMaxLines(model.maxLines());

            textViewLabel.setText(model.label());
            textInputLayout.setHint(model.hint());
        });

        // listen to changes in edit text, push them to
        // observer only in case if they are distinct
        RxTextView.afterTextChangeEvents(editText)
                .takeUntil(RxView.detaches(parent))
                .filter(event -> event.editable() != null)
                .debounce(512, TimeUnit.MILLISECONDS)
                .withLatestFrom(modelSubject, (event, model) ->
                        RowAction.create(model.uid(), event.editable().toString()))
                .skip(1)
                .distinctUntilChanged(RowAction::value)
                .subscribe(processor::onNext, throwable -> {
                    throw new OnErrorNotImplementedException(throwable);
                });

        // show and hide hint depending on focus state of the edittext.
        RxView.focusChanges(editText)
                .takeUntil(RxView.detaches(parent))
                .subscribe(hasFocus -> textInputLayout.setHintEnabled(
                        editText.hasFocus() || isEmpty(editText.getText())));
    }

    void update(@NonNull EditTextModel model) {
        modelSubject.onNext(model);
    }
}
