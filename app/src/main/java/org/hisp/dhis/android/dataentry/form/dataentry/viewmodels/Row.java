package org.hisp.dhis.android.dataentry.form.dataentry.viewmodels;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.commons.tuples.Pair;

import io.reactivex.observers.DisposableObserver;

public interface Row<VH extends RecyclerView.ViewHolder, VM extends FormItemViewModel> {

    @NonNull
    VH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    void onBindViewHolder(@NonNull VH viewHolder, @NonNull VM viewModel);
}