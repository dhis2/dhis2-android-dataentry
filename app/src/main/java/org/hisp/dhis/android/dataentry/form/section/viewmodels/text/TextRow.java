package org.hisp.dhis.android.dataentry.form.section.viewmodels.text;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.tuples.Pair;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.Row;

import io.reactivex.observers.DisposableObserver;

public class TextRow implements Row<TextViewHolder, TextViewModel> {

    @Override
    public TextViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new TextViewHolder(inflater.inflate(R.layout.recyclerview_row_textview, parent, false));
    }

    @Override
    public void onBindViewHolder(TextViewHolder viewHolder, TextViewModel viewModel,
                                 DisposableObserver<Pair<String, String>> onValueChangeObserver) {
        viewHolder.update(viewModel);
    }
}