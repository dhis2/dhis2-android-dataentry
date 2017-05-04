package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels.coordinate;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import static org.hisp.dhis.android.dataentry.commons.utils.StringUtils.isEmpty;

class CoordinateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textview_row_label)
    TextView label;

    @BindView(R.id.coordinate_row_latitude_textinputlayout)
    TextInputLayout latitudeInputLayout;

    @BindView(R.id.coordinate_row_longitude_textinputlayout)
    TextInputLayout longitudeInputLayout;

    @BindView(R.id.coordinate_row_latitude_edittext)
    EditText latitudeEditText;

    @BindView(R.id.coordinate_row_longitude_edittext)
    EditText longitudeEditText;

    private CoordinateViewModel viewModel;

    private final CompositeDisposable valueChangeObservers;
    private final Observable<Pair<String, String>> valueChangeObservable;

    CoordinateViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        valueChangeObservers = new CompositeDisposable();

        valueChangeObservable = RxTextView
                .afterTextChangeEvents(latitudeEditText)
                .skipInitialValue()
                .map(this::pairUidAndValue);

        longitudeEditText.setOnFocusChangeListener(this::toggleHintVisibility);
        latitudeEditText.setOnFocusChangeListener(this::toggleHintVisibility);
    }

    void update(@NonNull CoordinateViewModel viewModel,
            @NonNull DisposableObserver<Pair<String, String>> valueChangeObserver) {
        this.viewModel = viewModel;

        valueChangeObservers.clear();

        label.setText(viewModel.label());

        latitudeEditText.setText(viewModel.latitude());
        longitudeEditText.setText(viewModel.longitude());

        valueChangeObservers.add(valueChangeObservable.share().subscribeWith(valueChangeObserver));
    }

    @NonNull
    private Pair<String, String> pairUidAndValue(@NonNull TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
        String coordinateString = textViewAfterTextChangeEvent.editable() + "," + longitudeEditText.getText();
        return Pair.create(viewModel.uid(), coordinateString);
    }

    private void toggleHintVisibility(@NonNull View view, @NonNull Boolean hasFocus) {
        if (view.getId() == R.id.coordinate_row_latitude_textinputlayout) {
            if (hasFocus || isEmpty(latitudeEditText.getText())) {
                latitudeInputLayout.setHintEnabled(true);
            } else {
                latitudeInputLayout.setHintEnabled(false);
            }
        } else if (view.getId() == R.id.coordinate_row_longitude_textinputlayout) {
            if (hasFocus || isEmpty(longitudeEditText.getText())) {
                longitudeInputLayout.setHintEnabled(true);
            } else {
                longitudeInputLayout.setHintEnabled(false);
            }
        }
    }

}