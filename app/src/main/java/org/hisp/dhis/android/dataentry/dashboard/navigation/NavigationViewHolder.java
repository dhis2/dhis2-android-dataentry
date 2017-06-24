package org.hisp.dhis.android.dataentry.dashboard.navigation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.CircleView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class NavigationViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.status_background)
    CircleView statusBackground;

    @BindView(R.id.status_icon)
    ImageView statusIcon;

    @NonNull
    private final View view;

    @NonNull
    private final OnEventClickListener onEventClickListener;

    @NonNull
    private final EventSelection eventSelection;

    @NonNull
    private final Map<EventStatus, Drawable> statusIcons;

    @NonNull
    private final Map<EventStatus, Integer> statusColors;

    private EventViewModel eventViewModel;

    interface OnEventClickListener {
        void OnEventClicked(EventViewModel eventViewModel);
    }

    NavigationViewHolder(@NonNull View itemView, @NonNull OnEventClickListener onEventClickListener,
                         @NonNull EventSelection eventSelection) {
        super(itemView);
        this.view = itemView;
        ButterKnife.bind(this, itemView);
        this.onEventClickListener = onEventClickListener;
        this.eventSelection = eventSelection;
        statusIcons = new HashMap<>();
        statusColors = new HashMap<>();
        initCaches(itemView.getContext());
    }

    private void initCaches(Context context) {
        // TODO: icons and background colors are fixed pairs. Create a CompositeDrawable and reference the resource id
        // in the view model instead of the Event status (separation of concerns)
        statusIcons.put(EventStatus.ACTIVE, ContextCompat.getDrawable(context, R.drawable.ic_event_active));
        statusIcons.put(EventStatus.COMPLETED, ContextCompat.getDrawable(context, R.drawable.ic_event_completed));
        statusIcons.put(EventStatus.SCHEDULE, ContextCompat.getDrawable(context, R.drawable.ic_event_scheduled));
        statusIcons.put(EventStatus.SKIPPED, ContextCompat.getDrawable(context, R.drawable.ic_event_skipped));

        statusColors.put(EventStatus.ACTIVE, ContextCompat.getColor(context, R.color.color_active));
        statusColors.put(EventStatus.COMPLETED, ContextCompat.getColor(context, R.color.color_completed));
        statusColors.put(EventStatus.SCHEDULE, ContextCompat.getColor(context, R.color.color_schedule));
        statusColors.put(EventStatus.SKIPPED, ContextCompat.getColor(context, R.color.color_skipped));
    }

    void update(EventViewModel eventViewModel) {
        this.eventViewModel = eventViewModel;
        name.setText(eventViewModel.title());
        date.setText(eventViewModel.date());
        statusIcon.setImageDrawable(statusIcons.get(eventViewModel.eventStatus()));
        statusBackground.setFillColor(statusColors.get(eventViewModel.eventStatus()));
        if (isSelected(eventViewModel)) {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.data_entry_background));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.cardview_light_background));
        }
    }

    private boolean isSelected(EventViewModel eventViewModel) {
        return eventSelection.getSelectedEvent() != null
                && eventSelection.getSelectedEvent().equals(eventViewModel);
    }

    @OnClick(R.id.event_item)
    void onEventClick() {
        onEventClickListener.OnEventClicked(eventViewModel);
    }
}