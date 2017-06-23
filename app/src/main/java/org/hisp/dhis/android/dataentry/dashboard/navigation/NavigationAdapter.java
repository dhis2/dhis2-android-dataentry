package org.hisp.dhis.android.dataentry.dashboard.navigation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;

import java.util.ArrayList;
import java.util.List;

class NavigationAdapter extends RecyclerView.Adapter<NavigationViewHolder> implements EventSelection {

    @NonNull
    private final List<EventViewModel> events;

    @NonNull
    private final NavigationViewHolder.OnEventClickListener onEventClickListener;

    @Nullable
    private EventViewModel selectedEvent;

    NavigationAdapter(@NonNull NavigationViewHolder.OnEventClickListener onEventClickListener) {
        this.onEventClickListener = onEventClickListener;
        events = new ArrayList<>();
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NavigationViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_dashboard_event_item, parent, false),
                onEventClickListener, this);
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder holder, int position) {
        holder.update(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    void swap(@NonNull List<EventViewModel> events) {
        this.events.clear();
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    @Override
    public void setSelectedEvent(@NonNull EventViewModel selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    @Nullable
    @Override
    public EventViewModel getSelectedEvent() {
        return selectedEvent;
    }
}
