package org.hisp.dhis.android.dataentry.reports;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.CircleView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.hisp.dhis.android.dataentry.utils.StringUtils.htmlify;

class ReportsAdapter extends Adapter<ReportsAdapter.ReportViewHolder> {

    @NonNull
    private final LayoutInflater layoutInflater;

    @NonNull
    private final List<ReportViewModel> reportViewModels;

    ReportsAdapter(@NonNull Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.reportViewModels = new ArrayList<>();
    }

    @Override
    public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReportViewHolder(layoutInflater.inflate(
                R.layout.recyclerview_report_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ReportViewHolder viewHolder, int position) {
        viewHolder.update(reportViewModels.get(position));
    }

    @Override
    public int getItemCount() {
        return reportViewModels.size();
    }

    void swapData(@NonNull List<ReportViewModel> reports) {
        reportViewModels.clear();
        reportViewModels.addAll(reports);

        // ToDo: improve performance of RecyclerView
        notifyDataSetChanged();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {

//        @BindView(R.id.container_status_icon)
//        View containerStatus;

        @BindView(R.id.circleview_status_background)
        CircleView circleViewStatus;

        @BindView(R.id.imageview_status_icon)
        ImageView imageViewStatus;

        @BindView(R.id.textview_report_value_labels)
        TextView textViewValues;

        @BindView(R.id.button_delete)
        ImageButton buttonDelete;

        final Drawable drawableSent;
        final Drawable drawableOffline;
        final Drawable drawableError;

        final int colorSent;
        final int colorOffline;
        final int colorError;

        ReportViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            Context context = itemView.getContext();

            drawableSent = ContextCompat.getDrawable(context, R.drawable.ic_sent);
            drawableError = ContextCompat.getDrawable(context, R.drawable.ic_error);
            drawableOffline = ContextCompat.getDrawable(context, R.drawable.ic_offline);

            colorSent = ContextCompat.getColor(context, R.color.color_material_green);
            colorError = ContextCompat.getColor(context, R.color.color_material_red);
            colorOffline = ContextCompat.getColor(context, R.color.color_accent);
        }

        void update(@NonNull ReportViewModel reportViewModel) {
            textViewValues.setText(getLabels(reportViewModel));

            // set status
            switch (reportViewModel.status()) {
                case SYNCED: {
                    buttonDelete.setVisibility(View.INVISIBLE);
                    circleViewStatus.setFillColor(colorSent);
                    imageViewStatus.setImageDrawable(drawableSent);
                    break;
                }
                case TO_SYNC: {
                    buttonDelete.setVisibility(View.INVISIBLE);
                    circleViewStatus.setFillColor(colorOffline);
                    imageViewStatus.setImageDrawable(drawableOffline);
                    break;
                }
                case FAILED: {
                    buttonDelete.setVisibility(View.INVISIBLE);
                    circleViewStatus.setFillColor(colorError);
                    imageViewStatus.setImageDrawable(drawableError);
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }

        private Spanned getLabels(@NonNull ReportViewModel report) {
            return htmlify(report.labels());
        }
    }
}
