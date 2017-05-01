package org.hisp.dhis.android.dataentry.form.section.viewmodels.text;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.Row;

import io.reactivex.observers.DisposableObserver;

public class TextRow implements Row<TextViewHolder, TextViewModel> {

    @Override
    public TextViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new TextViewHolder(inflater.inflate(R.layout.recyclerview_row_textview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder viewHolder, @NonNull TextViewModel viewModel,
            @NonNull DisposableObserver<Pair<String, String>> onValueChangeObserver) {
        viewHolder.update(viewModel);
    }
}