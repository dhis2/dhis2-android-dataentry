package org.hisp.dhis.android.dataentry.reports;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hisp.dhis.android.dataentry.DhisApp;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.commons.ui.BaseFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

public final class ReportsFragment extends BaseFragment
        implements ReportsView, ReportsAdapter.OnReportViewModelClickListener {
    private static final String ARG_ENTITY_UID = "arg:entityUid";
    private static final String ARG_ENTITY_NAME = "arg:entityName";
    private static final String ARG_ENTITY_TYPE = "arg:entityType";

    @BindView(R.id.recyclerview_reports)
    RecyclerView recyclerViewReports;

    @Inject
    ReportsPresenter presenter;

    @Inject
    ReportsNavigator reportsNavigator;

    ReportsAdapter reportsAdapter;

    @NonNull
    public static ReportsFragment createForEvents(@NonNull String programUid, @NonNull String programName) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_ENTITY_UID, programUid);
        arguments.putString(ARG_ENTITY_TYPE, ReportViewModel.TYPE_EVENTS);
        arguments.putString(ARG_ENTITY_NAME, programName);

        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(arguments);

        return reportsFragment;
    }

    @NonNull
    public static ReportsFragment createForTeis(@NonNull String teUid, @NonNull String teName) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_ENTITY_UID, teUid);
        arguments.putString(ARG_ENTITY_TYPE, ReportViewModel.TYPE_TEIS);
        arguments.putString(ARG_ENTITY_NAME, teName);

        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(arguments);

        return reportsFragment;
    }

    @NonNull
    public static ReportsFragment createForEnrollments(@NonNull String teiUid, @NonNull String teName) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_ENTITY_UID, teiUid);
        arguments.putString(ARG_ENTITY_TYPE, ReportViewModel.TYPE_ENROLLMENTS);
        arguments.putString(ARG_ENTITY_NAME, teName);

        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(arguments);

        return reportsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ((DhisApp) context.getApplicationContext()).userComponent()
                .plus(new ReportsModule(getActivity(), getEntityUid(),
                        getEntityType(), getEntityName()))
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        bind(this, view);
        setUpRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onAttach(this);
    }

    @NonNull
    @Override
    public Consumer<List<ReportViewModel>> renderReportViewModels() {
        return reportViewModels -> reportsAdapter.swapData(reportViewModels);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        reportsAdapter = new ReportsAdapter(getContext(), this);
        recyclerViewReports.setLayoutManager(layoutManager);
        recyclerViewReports.setAdapter(reportsAdapter);
    }

    private String getEntityUid() {
        return getArguments().getString(ARG_ENTITY_UID, "");
    }

    private String getEntityType() {
        return getArguments().getString(ARG_ENTITY_TYPE, "");
    }

    private String getEntityName() {
        return getArguments().getString(ARG_ENTITY_NAME, "");
    }

    @Override
    public void onClick(@NonNull ReportViewModel reportViewModel) {
        reportsNavigator.navigateTo(reportViewModel.id());
    }
}
