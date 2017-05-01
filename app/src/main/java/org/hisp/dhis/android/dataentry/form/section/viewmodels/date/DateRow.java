package org.hisp.dhis.android.dataentry.form.section.viewmodels.date;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.Row;

import io.reactivex.observers.DisposableObserver;

import static org.hisp.dhis.android.dataentry.commons.utils.Preconditions.isNull;

public class DateRow implements Row<DateViewHolder, DateViewModel> {

    // we need fragment manager to display DatePickerDialogFragment
    private final FragmentManager fragmentManager;

    public DateRow(@NonNull FragmentManager fragmentManager) {
        this.fragmentManager = isNull(fragmentManager, "fragmentManager must not be null");
    }

    @Override
    @NonNull
    public DateViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new DateViewHolder(inflater.inflate(
                R.layout.recyclerview_row_date, parent, false), fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder viewHolder, @NonNull DateViewModel viewModel,
            @NonNull DisposableObserver<Pair<String, String>> onValueChangeObserver) {
        viewHolder.update(viewModel, onValueChangeObserver);
    }
}
