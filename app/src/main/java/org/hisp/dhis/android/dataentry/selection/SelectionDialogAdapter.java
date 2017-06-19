package org.hisp.dhis.android.dataentry.selection;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.R;

import java.util.ArrayList;
import java.util.List;

public class SelectionDialogAdapter extends RecyclerView.Adapter<SelectionDialogAdapter.SelectionViewHolder> {
    private final List<SelectionViewModel> selectionList;

    private final View.OnClickListener listener;

    public SelectionDialogAdapter(View.OnClickListener onClickListener) {
        this.selectionList = new ArrayList<>();
        this.listener = onClickListener;
    }

    public void update(List<SelectionViewModel> selectionList) {
        this.selectionList.clear();
        this.selectionList.addAll(selectionList);
        this.notifyDataSetChanged();
    }

    @Override
    public SelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_selection_list, parent, false);

        textView.setOnClickListener(listener);
        return new SelectionViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(SelectionViewHolder holder, int position) {
        SelectionViewModel model = selectionList.get(position);
        holder.textView.setText(model.label());
        holder.textView.setTag(model);
    }

    @Override
    public int getItemCount() {
        return selectionList.size();
    }

    public static class SelectionViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public SelectionViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof TextView) {
                textView = (TextView) itemView;
            }
        }
    }
}

