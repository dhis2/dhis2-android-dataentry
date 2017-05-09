package org.hisp.dhis.android.dataentry.form.dataentry.fields;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

public interface Row {

    @NonNull
    ViewHolder onCreate(@NonNull ViewGroup parent);

    void onBind(@NonNull ViewHolder viewHolder, @NonNull FieldViewModel viewModel);
}