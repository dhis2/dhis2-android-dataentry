package org.hisp.dhis.android.dataentry.selection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.selection.SelectionDialogAdapter.SelectionViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import rx.exceptions.OnErrorNotImplementedException;

final class SelectionDialogAdapter extends Adapter<SelectionViewHolder> {

    @NonNull
    private final LayoutInflater inflater;

    @NonNull
    private final List<SelectionViewModel> selectionList;

    @NonNull
    private final PublishSubject<SelectionViewModel> selectionViewSubject;

    SelectionDialogAdapter(@NonNull LayoutInflater inflater) {
        this.inflater = inflater;
        this.selectionList = new ArrayList<>();
        this.selectionViewSubject = PublishSubject.create();
    }

    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectionViewHolder(inflater.inflate(
                R.layout.recyclerview_selection_list, parent, false), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder holder, int position) {
        holder.update(selectionList.get(position));
    }

    @Override
    public int getItemCount() {
        return selectionList.size();
    }

    @NonNull
    Observable<SelectionViewModel> asObservable() {
        return selectionViewSubject;
    }

    void update(@NonNull List<SelectionViewModel> models) {
        selectionList.clear();
        selectionList.addAll(models);
        notifyDataSetChanged();
    }

    class SelectionViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        TextView textView;

        @Nullable
        SelectionViewModel selectionViewModel;

        @SuppressWarnings("CheckReturnValue")
        SelectionViewHolder(@NonNull View itemView, @NonNull ViewGroup parent) {
            super(itemView);
            textView = (TextView) itemView;

            RxView.clicks(itemView)
                    .takeUntil(RxView.detaches(parent))
                    .subscribe(click -> selectionViewSubject.onNext(selectionViewModel), throwable -> {
                        throw new OnErrorNotImplementedException(throwable);
                    });
        }

        void update(@NonNull SelectionViewModel viewModel) {
            textView.setTag(viewModel);
            textView.setText(viewModel.label());
            selectionViewModel = viewModel;
        }
    }
}

