package org.hisp.dhis.android.dataentry.main.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;
import org.hisp.dhis.android.dataentry.commons.ui.DividerDecoration;
import org.hisp.dhis.android.dataentry.form.dataentry.DataEntryActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

public class HomeFragment extends BaseFragment implements HomeView {

    @Inject
    HomePresenter homePresenter;

    @BindView(R.id.swiperefreshlayout_home)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.home_recyclerview)
    RecyclerView recyclerView;

    private HomeViewModelAdapter homeViewModelAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getUserComponent().plus(new HomeModule()).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);

        setupRecyclerView();
        setupSwipeRefreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        homePresenter.onAttach(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        homePresenter.onDetach();
    }

    private void setupRecyclerView() {
        homeViewModelAdapter = new HomeViewModelAdapter(getActivity());
        homeViewModelAdapter.setOnHomeItemClickListener(homeEntity -> {
            if (homeEntity.type() == HomeViewModel.Type.PROGRAM) {
                startActivity(DataEntryActivity.create(getActivity()));
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(homeViewModelAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(
                ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.color_primary);
    }

    @Override
    public Consumer<List<HomeViewModel>> swapData() {
        return homeEntities -> homeViewModelAdapter.swapData(homeEntities);
    }

    @Override
    public void renderError(String message) {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton(android.R.string.ok, null)
                .setTitle(getString(R.string.error_generic))
                .setMessage(message)
                .show();
    }
}