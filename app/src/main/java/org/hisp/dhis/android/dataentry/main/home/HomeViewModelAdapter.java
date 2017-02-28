/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.dataentry.main.home;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.views.FontTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.hisp.dhis.android.dataentry.utils.Preconditions.isNull;

public class HomeViewModelAdapter extends RecyclerView.Adapter {

    private final LayoutInflater layoutInflater;
    private final List<HomeViewModel> homeViewModels;
    private OnHomeItemClicked onHomeItemClickListener;

    public HomeViewModelAdapter(Context context) {
        isNull(context, "context must not be null");

        this.layoutInflater = LayoutInflater.from(context);
        this.homeViewModels = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeViewModelViewHolder(layoutInflater.inflate(
                R.layout.single_line_icon_with_text, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeViewModel homeViewModel = homeViewModels.get(position);
        if (holder instanceof HomeViewModelViewHolder) {
            ((HomeViewModelViewHolder) holder).update(homeViewModel);
        }
    }

    @Override
    public int getItemCount() {
        return homeViewModels.size();
    }

    public void swapData(@Nullable List<HomeViewModel> homeViewModels) {
        this.homeViewModels.clear();

        if (homeViewModels != null) {
            this.homeViewModels.addAll(homeViewModels);
        }

        notifyDataSetChanged();
    }

    public void setOnHomeItemClickListener(OnHomeItemClicked onHomeItemClicked) {
        this.onHomeItemClickListener = onHomeItemClicked;
    }

    public interface OnHomeItemClicked {
        void onHomeItemClicked(HomeViewModel homeViewModel);
    }

    final class HomeViewModelViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text)
        FontTextView title;

        @BindView(R.id.icon)
        ImageView icon;

        private HomeViewModel homeViewModel;

        HomeViewModelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void update(HomeViewModel homeViewModel) {
            this.homeViewModel = homeViewModel;

            title.setText(homeViewModel.title());

            int iconResourceId;
            if (homeViewModel.type() == HomeViewModel.Type.TRACKED_ENTITY) {
                iconResourceId = R.drawable.ic_widgets_black;
            } else {
                iconResourceId = R.drawable.ic_border_all_black;
            }

            icon.setImageResource(iconResourceId);
        }

        @OnClick(R.id.container)
        public void onHomeEntityClick() {
            if (onHomeItemClickListener != null) {
                onHomeItemClickListener.onHomeItemClicked(homeViewModel);
            }
        }
    }
}
