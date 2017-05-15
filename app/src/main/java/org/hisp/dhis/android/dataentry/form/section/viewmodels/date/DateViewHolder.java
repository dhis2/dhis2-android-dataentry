package org.hisp.dhis.android.dataentry.form.section.viewmodels.date;

import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

final class DateViewHolder extends RecyclerView.ViewHolder {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    // Views
    @BindView(R.id.textview_row_label)
    TextView textViewLabel;

    @BindView(R.id.row_date_picker_edit_text)
    EditText editText;

    private DateViewModel viewModel;
    private final FragmentManager fragmentManager;
    private Calendar calendar;

    private final Observable<Pair<String, String>> onValueChangeObservable;
    private final CompositeDisposable onValueChangeObservers;

    DateViewHolder(@NonNull View itemView, @NonNull FragmentManager fragmentManager) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        this.fragmentManager = fragmentManager;
        calendar = Calendar.getInstance();

        onValueChangeObservers = new CompositeDisposable();

        onValueChangeObservable = RxTextView.afterTextChangeEvents(editText)
                .map(textViewAfterTextChangeEvent -> Pair.create(viewModel.uid(),
                        textViewAfterTextChangeEvent.editable().toString()));

    }

    void update(@NonNull DateViewModel viewModel,
                @NonNull DisposableObserver<Pair<String, String>> onValueChangeObserver) {
        onValueChangeObservers.clear();

        this.viewModel = viewModel;
        textViewLabel.setText(viewModel.label());
        editText.setText(viewModel.value());

        onValueChangeObservers.add(onValueChangeObservable.share().subscribeWith(onValueChangeObserver));
    }

    @TargetApi(19)
    @OnClick({R.id.row_date_picker_edit_text, R.id.row_date_picker_button_pick})
    void showDatePicker() {
        DatePickerDialogFragment datePicker = DatePickerDialogFragment.newInstance(false);
        datePicker.setFormattedOnDateSetListener(editText::setText);
        datePicker.show(fragmentManager);
    }

    @OnClick(R.id.button_clear)
    void clearDate() {
        editText.setText("");
    }

    @OnClick(R.id.row_date_picker_button_today)
    void setTodaysDate() {
        calendar = Calendar.getInstance(); // refresh calendar in case day has shifted since instantiation
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        editText.setText(simpleDateFormat.format(calendar.getTime()));
    }
}