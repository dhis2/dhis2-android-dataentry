package org.hisp.dhis.android.dataentry.dashboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;

import java.util.ArrayList;
import java.util.List;

class DashboardAdapter extends RecyclerView.Adapter<DashboardViewHolder> {

    @NonNull
    private final List<EventViewModel> events;
    private final DashboardViewHolder.OnEventClickListener onEventClickListener;

    DashboardAdapter(DashboardViewHolder.OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
        events = new ArrayList<>();
    }

    @Override
    public DashboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DashboardViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_dashboard_event_item, parent, false), onEventClickListener);
    }

    @Override
    public void onBindViewHolder(DashboardViewHolder holder, int position) {
        holder.update(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    void swap(@NonNull List<EventViewModel> events) {
        this.events.clear();
        this.events.addAll(events);
    }
}
