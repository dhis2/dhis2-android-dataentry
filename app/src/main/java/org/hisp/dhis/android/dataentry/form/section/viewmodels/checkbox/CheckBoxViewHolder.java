package org.hisp.dhis.android.dataentry.form.section.viewmodels.checkbox;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxCompoundButton;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

final class CheckBoxViewHolder extends RecyclerView.ViewHolder {
    private static final String TRUE = "true";
    private static final String EMPTY_FIELD = "";

    @BindView(R.id.checkbox_row_checkbox)
    CheckBox checkBox;

    @BindView(R.id.textview_row_label)
    TextView textViewLabel;

    private CheckBoxViewModel viewModel;

    private final CompositeDisposable valueChangeObservers;
    private final Observable<Pair<String, String>> valueChangeObservable;

    CheckBoxViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        valueChangeObservers = new CompositeDisposable();

        valueChangeObservable = RxCompoundButton
                .checkedChanges(checkBox)
                .skipInitialValue()
                .map(this::pairUidAndValue);

        itemView.setOnClickListener(v -> checkBox.setChecked(!checkBox.isChecked()));
    }

    void update(@NonNull CheckBoxViewModel viewModel,
            @NonNull DisposableObserver<Pair<String, String>> observer) {

        valueChangeObservers.clear();

        this.viewModel = viewModel;
        textViewLabel.setText(viewModel.label());

        checkBox.setChecked(viewModel.value());

        valueChangeObservers.add(valueChangeObservable.share().subscribeWith(observer));
    }

    @NonNull
    private Pair<String, String> pairUidAndValue(Boolean checked) {
        return Pair.create(viewModel.uid(), checked ? TRUE : EMPTY_FIELD);
    }
}