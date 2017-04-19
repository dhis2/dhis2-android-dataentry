package org.hisp.dhis.android.dataentry.form.section.viewmodels.text;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.hisp.dhis.android.dataentry.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textview_row_label)
    TextView label;

    @BindView(R.id.textview_row_textview)
    TextView value;

    public TextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void update(TextViewModel viewModel) {
        label.setText(viewModel.label());
        value.setText(viewModel.value());
    }
}
