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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.BaseFragment;
import org.hisp.dhis.android.dataentry.commons.views.DividerDecoration;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class HomeFragment extends BaseFragment implements HomeView {

    private static final String LAYOUT_MANAGER_KEY = "LAYOUT_MANAGER_KEY";
    private static final String STATE_IS_REFRESHING = "state:isRefreshing";

    @Inject
    HomePresenter homePresenter;

    @BindView(R.id.swiperefreshlayout_home)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.home_recyclerview)
    RecyclerView recyclerView;

    private HomeEntityAdapter homeEntityAdapter;
    private AlertDialog alertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserComponent().plus(new HomeModule()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAdapter();
        setupSwipeRefreshLayout(savedInstanceState);

        recyclerView.setAdapter(homeEntityAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        if (savedInstanceState != null) {
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT_MANAGER_KEY));
        }

        if (savedInstanceState != null) {
            homeEntityAdapter.onRestoreInstanceState(
                    savedInstanceState.getBundle(HomeEntityAdapter.KEY_HOME_ENTITIES));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(
                ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
    }

    private void setupSwipeRefreshLayout(final Bundle savedInstanceState) {
        swipeRefreshLayout.setColorSchemeResources(R.color.color_primary);
        // swipeRefreshLayout.setOnRefreshListener() todo set this when Syncing Service is in place

        if (savedInstanceState != null) {
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(savedInstanceState
                    .getBoolean(STATE_IS_REFRESHING, false)));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        homePresenter.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        homePresenter.onAttach(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_REFRESHING, swipeRefreshLayout.isRefreshing());

        outState.putParcelable(HomeEntityAdapter.KEY_HOME_ENTITIES, homeEntityAdapter.onSaveInstanceState());
        outState.putParcelable(LAYOUT_MANAGER_KEY, recyclerView.getLayoutManager().onSaveInstanceState());

    }

    private void setupAdapter() {

        homeEntityAdapter = new HomeEntityAdapter(getActivity());
        homeEntityAdapter.setOnHomeItemClickListener(homeEntity -> {
            /*if (homeEntity.getType() == HomeEntity.HomeEntityType.TRACKED_ENTITY) {
                // todo go to TrackedEntity activity
            } else {
                // go to program activity
            }*/
        });
    }

    @Override
    public Consumer<List<HomeEntity>> swapData() {
        return homeEntities -> homeEntityAdapter.swapData(homeEntities);
    }

    public void showProgressBar() {

        // this workaround is necessary because of the message queue
        // implementation in android. If you will try to setRefreshing(true) right away,
        // this call will be placed in UI message queue by SwipeRefreshLayout BEFORE
        // message to hide progress bar which probably is created by layout
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
    }

    public void hideProgressBar() {
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public void showError(String message) {
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton(R.string.option_confirm, null);
            alertDialog = builder.create();
        }
        alertDialog.setTitle(getString(R.string.error_generic));
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}