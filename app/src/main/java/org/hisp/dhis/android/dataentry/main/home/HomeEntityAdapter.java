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
import android.os.Bundle;
import android.os.Parcelable;
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

public class HomeEntityAdapter extends RecyclerView.Adapter {
    public static final String KEY_HOME_ENTITIES = "key:homeEntities";
    private final LayoutInflater layoutInflater;
    private List<HomeEntity> homeEntities;
    private OnHomeItemClicked onHomeItemClickListener;

    public HomeEntityAdapter(Context context) {
        isNull(context, "context must not be null");

        this.layoutInflater = LayoutInflater.from(context);
        this.homeEntities = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeEntityViewHolder(layoutInflater.inflate(
                R.layout.recyclerview_row_home_entity_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeEntity homeEntity = homeEntities.get(position);
        if (holder instanceof HomeEntityViewHolder) {
            ((HomeEntityViewHolder) holder).update(homeEntity);
        }
    }

    @Override
    public int getItemCount() {
        return homeEntities.size();
    }

    public void swapData(@Nullable List<HomeEntity> homeEntities) {
        this.homeEntities.clear();

        if (homeEntities != null) {
            this.homeEntities.addAll(homeEntities);
        }

        notifyDataSetChanged();
    }

    public void setOnHomeItemClickListener(OnHomeItemClicked onHomeItemClicked) {
        this.onHomeItemClickListener = onHomeItemClicked;
    }

    public void filter(String query) {
        if (homeEntities != null && !homeEntities.isEmpty()) {
            List<HomeEntity> filteredLists = new ArrayList<>();
            for (HomeEntity homeEntity : homeEntities) {
                if (homeEntity.getTitle().contains(query)) {
                    filteredLists.add(homeEntity);
                }
            }
            swapData(filteredLists);
        }
    }

    public interface OnHomeItemClicked {
        void onHomeItemClicked(HomeEntity homeEntity);
    }

    public void onRestoreInstanceState(Bundle bundle) {
        homeEntities = bundle.getParcelableArrayList(KEY_HOME_ENTITIES);
        notifyDataSetChanged();
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_HOME_ENTITIES, (ArrayList<? extends Parcelable>) homeEntities);
        return bundle;
    }

    final class HomeEntityViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.home_entity_title)
        FontTextView title;

        @BindView(R.id.home_entity_icon)
        ImageView icon;

        private HomeEntity homeEntity;

        HomeEntityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void update(HomeEntity homeEntity) {
            this.homeEntity = homeEntity;

            title.setText(homeEntity.getTitle());

            int iconResourceId;
            if (homeEntity.getType() == HomeEntity.HomeEntityType.TRACKED_ENTITY) {
                iconResourceId = R.drawable.ic_widgets_black;
            } else {
                iconResourceId = R.drawable.ic_border_all_black;
            }

            icon.setImageResource(iconResourceId);
        }

        @OnClick(R.id.home_entity_container)
        public void onHomeEntityClick() {
            if (onHomeItemClickListener != null) {
                onHomeItemClickListener.onHomeItemClicked(homeEntity);
            }
        }
    }
}
