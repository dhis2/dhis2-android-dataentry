package org.hisp.dhis.android.dataentry.form.section.viewmodels.radiobutton;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxRadioGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

final class RadioButtonViewHolder extends RecyclerView.ViewHolder {

    static final String EMPTY_FIELD = "";
    static final String TRUE = "true";
    static final String FALSE = "false";

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

    private final CompositeDisposable valueChangeObservers;
    private final Observable<Pair<String, String>> valueChangeObservable;

    RadioButtonViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        valueChangeObservers = new CompositeDisposable();

        valueChangeObservable = RxRadioGroup
                .checkedChanges(radioGroup)
                .skipInitialValue()
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(this::pairUidAndValue);
    }

    void update(RadioButtonViewModel viewModel, DisposableObserver<Pair<String, String>> onValueChangeObserver) {
        this.viewModel = viewModel;

        valueChangeObservers.clear();

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

        valueChangeObservers.add(valueChangeObservable.share().subscribeWith(onValueChangeObserver));
    }

    @NonNull
    private Pair<String, String> pairUidAndValue(Integer checkedId) {
        if (checkedId == R.id.radiobutton_row_radiobutton_first) {
            return Pair.create(viewModel.uid(), TRUE);
        } else if (checkedId == R.id.radiobutton_row_radiobutton_second) {
            return Pair.create(viewModel.uid(), FALSE);
        }
        return Pair.create(viewModel.uid(), EMPTY_FIELD);
    }
}
