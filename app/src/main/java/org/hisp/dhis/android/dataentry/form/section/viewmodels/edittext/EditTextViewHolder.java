package org.hisp.dhis.android.dataentry.form.section.viewmodels.edittext;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;

import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

final class EditTextViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textview_row_label)
    TextView textViewLabel;

    @BindView(R.id.edittext_row_textinputlayout)
    TextInputLayout textInputLayout;

    @BindView(R.id.edittext_row_edittext)
    EditText editText;

    private EditTextViewModel viewModel;

    private final CompositeDisposable valueChangeObservers;
    private final Observable<Pair<String, String>> valueChangeObservable;

    EditTextViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        valueChangeObservers = new CompositeDisposable();

        valueChangeObservable = RxTextView
                .afterTextChangeEvents(editText)
                .skipInitialValue()
                .filter(valueHasChanged())
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(this::pairUidAndValue);

        editText.setOnFocusChangeListener((v, hasFocus) -> toggleHintVisibility(hasFocus));
    }

    void update(@NonNull EditTextViewModel viewModel,
            @NonNull DisposableObserver<Pair<String, String>> onValueChangeObserver) {

        this.viewModel = viewModel;

        valueChangeObservers.clear();

        textViewLabel.setText(viewModel.label());

        toggleHintVisibility(editText.hasFocus());
        textInputLayout.setHint(viewModel.hint());

        // AutoValue does not support array of non-primitives so we need to transform immutable list to array
        editText.setFilters(viewModel.inputFilters().toArray(new InputFilter[0]));

        editText.setText(viewModel.value());
        editText.setInputType(viewModel.inputType());
        editText.setMaxLines(viewModel.maxLines());

        valueChangeObservers.add(valueChangeObservable.share().subscribeWith(onValueChangeObserver));
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
        return textChangeEvent ->
                viewModel != null && !textChangeEvent.editable().toString().equals(viewModel.value());
    }

    @NonNull
    private Pair<String, String> pairUidAndValue(@NonNull TextViewAfterTextChangeEvent textChangeEvent) {
        String value;
        if (textChangeEvent.editable() == null) {
            value = "";
        } else {
            value = textChangeEvent.editable().toString();
        }
        return Pair.create(viewModel.uid(), value);
    }
}
